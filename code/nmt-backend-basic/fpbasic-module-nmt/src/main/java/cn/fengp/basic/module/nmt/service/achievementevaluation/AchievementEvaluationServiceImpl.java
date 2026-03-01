package cn.fengp.basic.module.nmt.service.achievementevaluation;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation.ObjectiveEvaluationDO;
import cn.fengp.basic.module.nmt.service.courseobjective.CourseObjectiveService;
import cn.fengp.basic.module.nmt.service.evaluatemode.EvaluateModeService;
import cn.fengp.basic.module.nmt.service.objectiveevaluation.ObjectiveEvaluationService;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Function;
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
    @Resource
    private ObjectiveEvaluationService objectiveEvaluationService;
    @Resource
    private CourseObjectiveService courseObjectiveService;

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
     * 判断新增或者更新
     * 1.新增
     *  1.1新增达成度评价
     *  1.2新增课程目标达成度评价
     * 2.更新
     *  2.1更新达成度评价
     *  2.2更新课程目标达成度评价
     * @param createReqVO
     * @return
     */
    @Override
    public Long saveAchievementEvaluation(AchievementEvaluationSaveExReqVO createReqVO) {
        //判断参数
        String objectiveEvaluations = createReqVO.getObjectiveEvaluations();
        if(!StringUtils.hasText(objectiveEvaluations)){
            throw ServiceExceptionUtil.invalidParamException("课程目标达成度评价不能为空");
        }
        //解析课程目标达成度评价
        JSONObject objEvaluations = JSON.parseObject(objectiveEvaluations);
        AchievementEvaluationDO achievementEvaluation = BeanUtils.toBean(createReqVO, AchievementEvaluationDO.class);
        //1.新增
        if(Objects.isNull(achievementEvaluation.getId())){
            // 1.1 新增达成度评价
            achievementEvaluationMapper.insert(achievementEvaluation);
            // 1.2 新增课程目标达成度评价
            List<ObjectiveEvaluationDO> objectiveEvaluationDOS = new ArrayList<>();
            // 查出所有key的课程目标
            List<Long> objectIds = objEvaluations.keySet().stream().map(Long::valueOf).collect(Collectors.toList());
            Map<Long, CourseObjectiveDO> objectiveDOMap = courseObjectiveService.getCourseObjectiveByIds(objectIds).stream().collect(Collectors.toMap(CourseObjectiveDO::getId, Function.identity()));
            // 新增目标达成度集合数据
            objEvaluations.forEach((key,value) -> {
                ObjectiveEvaluationDO oeDo = new ObjectiveEvaluationDO();
                oeDo.setEvaluationId(achievementEvaluation.getId());//达成度评价
                oeDo.setObjectiveId(Long.valueOf(key));//课程目标
                CourseObjectiveDO objective = objectiveDOMap.get(Long.valueOf(key));
                oeDo.setObjectiveName(objective.getName());//课程目标名称
                oeDo.setObjectiveContent(objective.getContent());//课程目标内容
                oeDo.setComment(value.toString());//课程目标评价内容
                objectiveEvaluationDOS.add(oeDo);
            });
            if(!CollectionUtils.isEmpty(objectiveEvaluationDOS)){
                objectiveEvaluationService.saveOrUpdateObjectiveEvaluationBatch(objectiveEvaluationDOS);
            }
        }else {
            //2.更新
            // 2.1 更新达成度评价
            achievementEvaluationMapper.updateById(achievementEvaluation);
            // 2.2 更新课程目标达成度评价
            List<ObjectiveEvaluationDO> existAchievementEvaluations = objectiveEvaluationService.getByAchievementEvaluation(achievementEvaluation.getId());
            for (ObjectiveEvaluationDO existDO : existAchievementEvaluations) {
                //更新课程目标评价内容
                Object comment = objEvaluations.get(existDO.getObjectiveId().toString());
                if(Objects.nonNull(comment)){
                    existDO.setComment(comment.toString());
                }
            }
            if(!CollectionUtils.isEmpty(existAchievementEvaluations)){
                objectiveEvaluationService.saveOrUpdateObjectiveEvaluationBatch(existAchievementEvaluations);
            }
        }
        // 返回
        return achievementEvaluation.getId();
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
     * 查询课程目标达成评价结果
     *  1.评价矩阵内容、矩阵内容总分数、每个学生的评价矩阵内容对应的总分、考核方式权重(一个sql查出)
     *  2.学生课程目标达成度
     *      单个目标达成度 = 求和(学生矩阵内容分数/矩阵内容分数 x 对应考核方式权重)
     *      -->CO_j = Σ( AvgScore(j,i) ÷ DesignScore(j,i) × Wi )
     *  3.总体课程目标达成度
     *      总体课程目标达成度 = 求和(矩阵内容平均分/矩阵内容分数 x 对应考核方式权重)
     *  4.报告评价内容
     * @param courseId
     * @param classId
     * @return
     */
    @Override
    public JSONObject getObjectiveAchievementEvaluation(Long courseId, Long classId) {
        if (Objects.isNull(courseId)){
            throw ServiceExceptionUtil.invalidParamException("课程标识不能为空");
        }
        if (Objects.isNull(classId)){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        //1.评价矩阵内容、矩阵内容总分数、每个学生的评价矩阵内容对应的总分、考核方式权重
        List<JSONObject> stuObjModeScoreList = achievementEvaluationMapper.getStuObjModeScoreList(courseId,classId);
        if(CollectionUtils.isEmpty(stuObjModeScoreList)){
            return new JSONObject();
        }
        //2.学生课程目标达成度
        List<JSONObject> stuObjRateList = this.calculateStuObjAcheRate(stuObjModeScoreList);
        //3.总体课程目标达成度
        List<JSONObject> courseObjRateList = this.calculateCourseObjAcheRate(stuObjModeScoreList);
        //4.报告评价内容
        LambdaQueryWrapperX<AchievementEvaluationDO> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(AchievementEvaluationDO::getCourseId, courseId).eq(AchievementEvaluationDO::getClassId, classId);
        AchievementEvaluationDO achievementEvaluationDO = achievementEvaluationMapper.selectOne(wrapperX);

        JSONObject evaluation = new JSONObject();
        evaluation.put("stuObjRateList",stuObjRateList);
        evaluation.put("courseObjRateList",courseObjRateList);
        evaluation.put("achiEval",achievementEvaluationDO);
        return evaluation;
    }


    /**
     * 计算学生课程目标达成度
     * @param stuObjModeScoreList
     * @return
     */
   private List<JSONObject> calculateStuObjAcheRate(List<JSONObject> stuObjModeScoreList) {
        return stuObjModeScoreList.stream()
                .collect(Collectors.groupingBy(
                        obj -> obj.getString("studentId") + "_" + obj.getString("objectiveId")
                ))
                .values()
                .stream()
                .map(groupList -> {
                    //分组第一个元素
                    JSONObject first = groupList.get(0);
                    //分组所有元素公式计算目标达成度(集合参数在查询sql中已经通过stuId,objectiveId,modeId分组，求和)
                    BigDecimal sum = groupList.stream()
                            .map(obj -> {
                                BigDecimal achscore = obj.getBigDecimal("achscore");
                                BigDecimal omscore = obj.getBigDecimal("omscore");
                                BigDecimal weight = obj.getBigDecimal("weight");
                                if (achscore == null) achscore = BigDecimal.ZERO;
                                if (omscore == null || omscore.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;
                                if (weight == null) weight = BigDecimal.ZERO;
                                return achscore.divide(omscore, 10, RoundingMode.HALF_UP).multiply(weight);
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)//weight保存的是百分制
                            .setScale(2, RoundingMode.HALF_UP);

                    JSONObject obj = new JSONObject();
                    obj.put("studentName", first.getString("studentName"));
                    obj.put("studentId", first.getString("studentId"));
                    obj.put("objectiveId", first.getString("objectiveId"));
                    obj.put("objectiveName", first.getString("objectiveName"));
                    obj.put("objRate", sum);
                    return obj;
                })
                .collect(Collectors.toList());
    }

    /**
     * 计算课程目标达成度
     * @param stuObjModeScoreList
     * @return
     */
    private List<JSONObject> calculateCourseObjAcheRate(List<JSONObject> stuObjModeScoreList) {
        // 1. 按 objectiveId + modeId 分组，计算 avscore 和 mode 达成比
        List<JSONObject> modeAchievementList = stuObjModeScoreList.stream()
                //课程目标、考核方式分组
                .collect(Collectors.groupingBy(obj -> obj.getString("objectiveId") + "_" + obj.getString("modeId")))
                .values()
                .stream()
                .map(groupList -> {
                    JSONObject first = groupList.get(0);
                    BigDecimal omscore = first.getBigDecimal("omscore");//矩阵分数
                    BigDecimal weight = first.getBigDecimal("weight");//考核方式权重
                    if (omscore == null || omscore.compareTo(BigDecimal.ZERO) == 0) omscore = BigDecimal.ONE; // 避免除零
                    if (weight == null) weight = BigDecimal.ZERO;

                    //求出每个课程目标对应具体考核方式的平均值
                    BigDecimal avscore = groupList.stream()
                            .map(obj -> {
                                BigDecimal achscore = obj.getBigDecimal("achscore");
                                return achscore == null ? BigDecimal.ZERO : achscore;
                            })
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                            .divide(new BigDecimal(groupList.size()), 10, RoundingMode.HALF_UP);//保留10位小数

                    // 课程目标具体考核方式达成比 = avscore / omscore * weight
                    BigDecimal achRatio = avscore.divide(omscore, 10, RoundingMode.HALF_UP)
                                                .multiply(weight)
                                                .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);//weight保存的是百分制;

                    JSONObject obj = new JSONObject();
                    obj.put("objectiveId", first.getString("objectiveId"));
                    obj.put("objectiveName", first.getString("objectiveName"));
                    obj.put("content", first.getString("content"));
                    obj.put("modeName", first.getString("modeName"));
                    obj.put("weight", weight);
                    obj.put("expectValue", first.getBigDecimal("expectValue").setScale(2, RoundingMode.HALF_UP));
                    obj.put("omscore", omscore);
                    obj.put("avscore", avscore.setScale(2, RoundingMode.HALF_UP));
                    obj.put("achRatio", achRatio.setScale(10, RoundingMode.HALF_UP)); // 保留较高精度用于后续求和
                    return obj;
                })
                .sorted(Comparator.comparing(obj -> obj.getString("objectiveId")))//按照课程目标排序
                .collect(Collectors.toList());
        // 2. 按课程目标分组汇总 对应所有考核方式达成比 求和
        Map<String, BigDecimal> totalAchMap = modeAchievementList.stream()
                .collect(Collectors.groupingBy(
                        obj -> obj.getString("objectiveId"),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                obj -> obj.getBigDecimal("achRatio"),
                                BigDecimal::add
                        )
                ));
        // 3. 设置 课程目标达成度 totalObjRate 保留两位小数
        modeAchievementList.forEach(obj -> {
            BigDecimal total = totalAchMap.get(obj.getString("objectiveId"));
            obj.put("totalObjRate", total.setScale(2, RoundingMode.HALF_UP));
            obj.remove("achRatio"); // 可选，清理中间字段
        });

        return modeAchievementList;
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