package cn.fengp.basic.module.nmt.service.achievementevaluation;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.module.nmt.service.evaluatemode.EvaluateModeService;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.achievementevaluation.AchievementEvaluationDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.achievementevaluation.AchievementEvaluationMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 达成度评价 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class AchievementEvaluationServiceImpl implements AchievementEvaluationService {

    @Resource
    private AchievementEvaluationMapper achievementEvaluationMapper;
    @Resource
    private EvaluateModeService evaluateModeService;

    @Override
    public Long createAchievementEvaluation(AchievementEvaluationSaveReqVO createReqVO) {
        // 插入
        AchievementEvaluationDO achievementEvaluation = BeanUtils.toBean(createReqVO, AchievementEvaluationDO.class);
        achievementEvaluationMapper.insert(achievementEvaluation);

        // 返回
        return achievementEvaluation.getId();
    }

    @Override
    public void updateAchievementEvaluation(AchievementEvaluationSaveReqVO updateReqVO) {
        // 校验存在
        validateAchievementEvaluationExists(updateReqVO.getId());
        // 更新
        AchievementEvaluationDO updateObj = BeanUtils.toBean(updateReqVO, AchievementEvaluationDO.class);
        achievementEvaluationMapper.updateById(updateObj);
    }

    @Override
    public void deleteAchievementEvaluation(Long id) {
        // 校验存在
        validateAchievementEvaluationExists(id);
        // 删除
        achievementEvaluationMapper.deleteById(id);
    }

    @Override
        public void deleteAchievementEvaluationListByIds(List<Long> ids) {
        // 删除
        achievementEvaluationMapper.deleteByIds(ids);
        }


    private void validateAchievementEvaluationExists(Long id) {
        if (achievementEvaluationMapper.selectById(id) == null) {
            throw exception(ACHIEVEMENT_EVALUATION_NOT_EXISTS);
        }
    }

    @Override
    public AchievementEvaluationDO getAchievementEvaluation(Long id) {
        return achievementEvaluationMapper.selectById(id);
    }

    @Override
    public PageResult<AchievementEvaluationDO> getAchievementEvaluationPage(AchievementEvaluationPageReqVO pageReqVO) {
        return achievementEvaluationMapper.selectPage(pageReqVO);
    }

    /**
     * 获取课程总评分数
     *   1.计算每个学生的总评分数
     *      1.1 计算学生每个考核方式的分数（考核方式总得分/考核总分 x 考核方式权重 （权重默认百分制））
     *      1.2 计算学生所有考核方式之和（每个学生、每个考核方式总得分之和）
     *   2.分别求最值分数、平均分
     *   3.根据分数段人数求占比
     * @param courseId
     * @param classId
     * @return
     */
    @Override
    public JSONObject getCourseOverallScore(Long courseId, Long classId) {
        if (Objects.isNull(courseId)){
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        if (Objects.isNull(classId)){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        //1.计算每个学生的总评分数
        //每个学生、每个考核方式得分占比 = 考核方式总得分/考核总分
        List<JSONObject> stuModeScoreRateList = achievementEvaluationMapper.getStuModeScoreRateList(courseId,classId);
        //课程所有考核方式
        List<EvaluateModeDO> evaluateModeList = evaluateModeService.listEvaluateMode(courseId);

        //1.1 计算学生每个考核方式的分数（考核方式总得分/考核总分 x 考核方式权重 （权重默认百分制））
        for (JSONObject stuModeScoreRate : stuModeScoreRateList) {
            BigDecimal modeScoreRate = stuModeScoreRate.getBigDecimal("modeScoreRate");
            //获取考核方式权重
            Optional<EvaluateModeDO> modeOpt = evaluateModeList.stream()
                    .filter(mode -> mode.getId().equals(stuModeScoreRate.getLong("modeId"))).findFirst();
            if (modeOpt.isPresent()) {
                BigDecimal modeWeight = modeOpt.get().getWeight();
                //权重默认百分制
                stuModeScoreRate.put("modeTotalScore", modeScoreRate.multiply(modeWeight));
            }else {
                stuModeScoreRate.put("modeTotalScore", 0);
            }
        }
        //1.2 计算学生所有考核方式之和（每个学生、每个考核方式总得分之和）
        //总分集合(四舍五入两位小数)
        List<BigDecimal> totalScoreList =
                stuModeScoreRateList.stream()
                        .collect(Collectors.groupingBy(
                                obj -> obj.getLong("studentId"),
                                Collectors.reducing(
                                        BigDecimal.ZERO,//初始值
                                        obj -> obj.getBigDecimal("modeTotalScore"),
                                        BigDecimal::add
                                )
                        ))
                        .values()
                        .stream()
                        .map(v -> v.setScale(2, RoundingMode.HALF_UP))//四舍五入保留两位小数
                        .collect(Collectors.toList());
        JSONObject result = new JSONObject();

        //2.分别求最值分数、平均分，保留两个小数
        this.calcMaxMinAvg(totalScoreList,result);

        //3.根据分数段人数求占比
        this.calcScoreRate(totalScoreList,result);
        return result;
    }


    /**
     * 计算最值，平均值，保留两个小数
     * @param scores
     * @param result
     * @return
     */
    private JSONObject calcMaxMinAvg(List<BigDecimal> scores,JSONObject result) {
        if (CollectionUtils.isEmpty(scores)) {
            result.put("max", BigDecimal.ZERO);
            result.put("min", BigDecimal.ZERO);
            result.put("avg", BigDecimal.ZERO);
            return result;
        }
        BigDecimal max = scores.stream()
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal min = scores.stream()
                .min(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal sum = scores.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal avg = sum.divide(BigDecimal.valueOf(scores.size()), 2, RoundingMode.HALF_UP);
        result.put("max", max);
        result.put("min", min);
        result.put("avg", avg);
        return result;
    }

    /**
     * 计算分数段人数占比
     * @param scores
     * @param result
     * @return
     */
    private JSONObject calcScoreRate(List<BigDecimal> scores,JSONObject result) {
        if (CollectionUtils.isEmpty(scores)) {
            result.put("90-100", "0.0");
            result.put("80-89", "0.0");
            result.put("70-79", "0.0");
            result.put("60-69", "0.0");
            result.put("≤59", "0.0");
            return result;
        }
        //学生总数
        int total = scores.size();
        //每个分数段学生人数
        long count90 = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(90)) >= 0).count();
        long count80 = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(80)) >= 0 && s.compareTo(BigDecimal.valueOf(90)) < 0).count();
        long count70 = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(70)) >= 0 && s.compareTo(BigDecimal.valueOf(80)) < 0).count();
        long count60 = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) >= 0 && s.compareTo(BigDecimal.valueOf(70)) < 0).count();
        long count59 = scores.stream().filter(s -> s.compareTo(BigDecimal.valueOf(60)) < 0).count();
        result.put("90-100", formatRate(count90, total));
        result.put("80-89", formatRate(count80, total));
        result.put("70-79", formatRate(count70, total));
        result.put("60-69", formatRate(count60, total));
        result.put("≤59", formatRate(count59, total));
        return result;
    }


    /**
     * 占比格式化(格式：6.6%)
     * @param count
     * @param total
     * @return
     */
    private String formatRate(long count, int total) {
        if (total == 0) return "0.0";
        return BigDecimal.valueOf(count)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 1, RoundingMode.HALF_UP).toString();
    }

}