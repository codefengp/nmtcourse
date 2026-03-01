package cn.fengp.basic.module.nmt.service.objectiveevaluation;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation.ObjectiveEvaluationDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.objectiveevaluation.ObjectiveEvaluationMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程目标达成度评价 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class ObjectiveEvaluationServiceImpl implements ObjectiveEvaluationService {

    @Resource
    private ObjectiveEvaluationMapper objectiveEvaluationMapper;

    @Override
    public Long createObjectiveEvaluation(ObjectiveEvaluationSaveReqVO createReqVO) {
        // 插入
        ObjectiveEvaluationDO objectiveEvaluation = BeanUtils.toBean(createReqVO, ObjectiveEvaluationDO.class);
        objectiveEvaluationMapper.insert(objectiveEvaluation);

        // 返回
        return objectiveEvaluation.getId();
    }

    public int saveOrUpdateObjectiveEvaluationBatch(List<ObjectiveEvaluationDO> list) {
        if(CollectionUtils.isEmpty(list)){
            throw ServiceExceptionUtil.invalidParamException("课程目标达成度评价集合不能为空");
        }
        objectiveEvaluationMapper.insertOrUpdate(list);
        // 返回
        return list.size();
    }

    @Override
    public List<ObjectiveEvaluationDO> getByAchievementEvaluation(Long id) {
        LambdaQueryWrapperX<ObjectiveEvaluationDO> wrapperX = new LambdaQueryWrapperX<>();
        wrapperX.eq(ObjectiveEvaluationDO::getEvaluationId, id);
        return objectiveEvaluationMapper.selectList(wrapperX);
    }

    @Override
    public void updateObjectiveEvaluation(ObjectiveEvaluationSaveReqVO updateReqVO) {
        // 校验存在
        validateObjectiveEvaluationExists(updateReqVO.getId());
        // 更新
        ObjectiveEvaluationDO updateObj = BeanUtils.toBean(updateReqVO, ObjectiveEvaluationDO.class);
        objectiveEvaluationMapper.updateById(updateObj);
    }

    @Override
    public void deleteObjectiveEvaluation(Long id) {
        // 校验存在
        validateObjectiveEvaluationExists(id);
        // 删除
        objectiveEvaluationMapper.deleteById(id);
    }

    @Override
        public void deleteObjectiveEvaluationListByIds(List<Long> ids) {
        // 删除
        objectiveEvaluationMapper.deleteByIds(ids);
        }


    private void validateObjectiveEvaluationExists(Long id) {
        if (objectiveEvaluationMapper.selectById(id) == null) {
            throw exception(OBJECTIVE_EVALUATION_NOT_EXISTS);
        }
    }

    @Override
    public ObjectiveEvaluationDO getObjectiveEvaluation(Long id) {
        return objectiveEvaluationMapper.selectById(id);
    }

    @Override
    public PageResult<ObjectiveEvaluationDO> getObjectiveEvaluationPage(ObjectiveEvaluationPageReqVO pageReqVO) {
        return objectiveEvaluationMapper.selectPage(pageReqVO);
    }

}