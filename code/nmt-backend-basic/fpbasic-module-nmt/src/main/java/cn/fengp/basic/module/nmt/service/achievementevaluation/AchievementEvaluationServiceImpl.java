package cn.fengp.basic.module.nmt.service.achievementevaluation;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
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

}