package cn.fengp.basic.module.nmt.service.evaluatemode;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.evaluatemode.EvaluateModeMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 考核评价方式 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class EvaluateModeServiceImpl implements EvaluateModeService {

    @Resource
    private EvaluateModeMapper evaluateModeMapper;

    @Override
    public Long createEvaluateMode(EvaluateModeSaveReqVO createReqVO) {
        // 插入
        EvaluateModeDO evaluateMode = BeanUtils.toBean(createReqVO, EvaluateModeDO.class);
        evaluateModeMapper.insert(evaluateMode);

        // 返回
        return evaluateMode.getId();
    }

    @Override
    public void updateEvaluateMode(EvaluateModeSaveReqVO updateReqVO) {
        // 校验存在
        validateEvaluateModeExists(updateReqVO.getId());
        // 更新
        EvaluateModeDO updateObj = BeanUtils.toBean(updateReqVO, EvaluateModeDO.class);
        evaluateModeMapper.updateById(updateObj);
    }

    @Override
    public void deleteEvaluateMode(Long id) {
        // 校验存在
        validateEvaluateModeExists(id);
        // 删除
        evaluateModeMapper.deleteById(id);
    }

    @Override
        public void deleteEvaluateModeListByIds(List<Long> ids) {
        // 删除
        evaluateModeMapper.deleteByIds(ids);
        }


    private void validateEvaluateModeExists(Long id) {
        if (evaluateModeMapper.selectById(id) == null) {
            throw exception(EVALUATE_MODE_NOT_EXISTS);
        }
    }

    @Override
    public EvaluateModeDO getEvaluateMode(Long id) {
        return evaluateModeMapper.selectById(id);
    }

    @Override
    public PageResult<EvaluateModeDO> getEvaluateModePage(EvaluateModePageReqVO pageReqVO) {
        return evaluateModeMapper.selectPage(pageReqVO);
    }

}