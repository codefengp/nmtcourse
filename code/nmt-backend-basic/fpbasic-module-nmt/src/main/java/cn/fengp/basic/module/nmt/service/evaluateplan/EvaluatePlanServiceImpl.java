package cn.fengp.basic.module.nmt.service.evaluateplan;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.evaluateplan.EvaluatePlanMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程考核计划 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class EvaluatePlanServiceImpl implements EvaluatePlanService {

    @Resource
    private EvaluatePlanMapper evaluatePlanMapper;

    @Override
    public Long createEvaluatePlan(EvaluatePlanSaveReqVO createReqVO) {
        // 插入
        EvaluatePlanDO evaluatePlan = BeanUtils.toBean(createReqVO, EvaluatePlanDO.class);
        evaluatePlanMapper.insert(evaluatePlan);

        // 返回
        return evaluatePlan.getId();
    }

    @Override
    public void updateEvaluatePlan(EvaluatePlanSaveReqVO updateReqVO) {
        // 校验存在
        validateEvaluatePlanExists(updateReqVO.getId());
        // 更新
        EvaluatePlanDO updateObj = BeanUtils.toBean(updateReqVO, EvaluatePlanDO.class);
        evaluatePlanMapper.updateById(updateObj);
    }

    @Override
    public void deleteEvaluatePlan(Long id) {
        // 校验存在
        validateEvaluatePlanExists(id);
        // 删除
        evaluatePlanMapper.deleteById(id);
    }

    @Override
        public void deleteEvaluatePlanListByIds(List<Long> ids) {
        // 删除
        evaluatePlanMapper.deleteByIds(ids);
        }


    private void validateEvaluatePlanExists(Long id) {
        if (evaluatePlanMapper.selectById(id) == null) {
            throw exception(EVALUATE_PLAN_NOT_EXISTS);
        }
    }

    @Override
    public EvaluatePlanDO getEvaluatePlan(Long id) {
        return evaluatePlanMapper.selectById(id);
    }

    @Override
    public PageResult<EvaluatePlanDO> getEvaluatePlanPage(EvaluatePlanPageReqVO pageReqVO) {
        return evaluatePlanMapper.selectPage(pageReqVO);
    }

}