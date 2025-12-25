package cn.fengp.basic.module.nmt.service.objectivemode;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.objectivemode.ObjectiveModeMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程目标考核评价 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class ObjectiveModeServiceImpl implements ObjectiveModeService {

    @Resource
    private ObjectiveModeMapper objectiveModeMapper;

    @Override
    public Long createObjectiveMode(ObjectiveModeSaveReqVO createReqVO) {
        // 插入
        ObjectiveModeDO objectiveMode = BeanUtils.toBean(createReqVO, ObjectiveModeDO.class);
        objectiveModeMapper.insert(objectiveMode);

        // 返回
        return objectiveMode.getId();
    }

    @Override
    public void updateObjectiveMode(ObjectiveModeSaveReqVO updateReqVO) {
        // 校验存在
        validateObjectiveModeExists(updateReqVO.getId());
        // 更新
        ObjectiveModeDO updateObj = BeanUtils.toBean(updateReqVO, ObjectiveModeDO.class);
        objectiveModeMapper.updateById(updateObj);
    }

    @Override
    public void deleteObjectiveMode(Long id) {
        // 校验存在
        validateObjectiveModeExists(id);
        // 删除
        objectiveModeMapper.deleteById(id);
    }

    @Override
        public void deleteObjectiveModeListByIds(List<Long> ids) {
        // 删除
        objectiveModeMapper.deleteByIds(ids);
        }


    private void validateObjectiveModeExists(Long id) {
        if (objectiveModeMapper.selectById(id) == null) {
            throw exception(OBJECTIVE_MODE_NOT_EXISTS);
        }
    }

    @Override
    public ObjectiveModeDO getObjectiveMode(Long id) {
        return objectiveModeMapper.selectById(id);
    }

    @Override
    public PageResult<ObjectiveModeDO> getObjectiveModePage(ObjectiveModePageReqVO pageReqVO) {
        return objectiveModeMapper.selectPage(pageReqVO);
    }

}