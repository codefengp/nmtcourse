package cn.fengp.basic.module.nmt.service.evaluatemode;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.EvaluateModePageReqVO;
import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.EvaluateModeSaveReqVO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.module.nmt.dal.mysql.evaluatemode.EvaluateModeMapper;
import cn.fengp.basic.module.nmt.service.objectivemode.ObjectiveModeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.EVALUATE_MODE_NOT_EXISTS;

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

    @Resource
    private ObjectiveModeService objectiveModeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEvaluateMode(EvaluateModeSaveReqVO createReqVO) {
        // 插入
        EvaluateModeDO evaluateMode = BeanUtils.toBean(createReqVO, EvaluateModeDO.class);
        evaluateModeMapper.insert(evaluateMode);
        //新增矩阵数据
        objectiveModeService.checkWithAddEvaluateMode(evaluateMode.getId(),false);
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
        //删除矩阵数据
        objectiveModeService.checkWithRemoveEvaluateMode(id,false);
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

    @Override
    public List<EvaluateModeDO> getEvaluateModeList() {
         return evaluateModeMapper.selectList();
    }

}
