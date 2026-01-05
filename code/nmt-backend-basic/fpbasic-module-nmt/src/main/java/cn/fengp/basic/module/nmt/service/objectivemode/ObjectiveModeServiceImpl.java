package cn.fengp.basic.module.nmt.service.objectivemode;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.ObjectiveModePageReqVO;
import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.ObjectiveModeSaveReqVO;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeDO;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeExDO;
import cn.fengp.basic.module.nmt.dal.mysql.courseobjective.CourseObjectiveMapper;
import cn.fengp.basic.module.nmt.dal.mysql.evaluatemode.EvaluateModeMapper;
import cn.fengp.basic.module.nmt.dal.mysql.objectivemode.ObjectiveModeMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.OBJECTIVE_MODE_NOT_EXISTS;

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
    @Resource
    private CourseObjectiveMapper courseObjectiveMapper;
    @Resource
    private EvaluateModeMapper evaluateModeMapper;

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

    /**
     * 检查并新增考核评价方式矩阵数据
     * @param id 课程目标id/考核方式id
     * @param isObjType 是否课程目标
     *   1.新增课程目标
     *       1.1 查所有考核方式
     *       1.2 新增中间表数据
     *   2.新增考核方式
     *      2.1 查所有课程目标
     *      2.2 新增中间表数据
     */
    @Override
    public void checkWithAddEvaluateMode(Long courseId,Long id, boolean isObjType) {
        // 查询课程所有目标/考核方式
        Long[] targetIds = isObjType ?
                evaluateModeMapper.selectList(new LambdaQueryWrapper<>(EvaluateModeDO.class).eq(EvaluateModeDO::getCourseId,courseId))
                        .stream().map(EvaluateModeDO::getId).toArray(Long[]::new)
                : courseObjectiveMapper.selectList(new LambdaQueryWrapper<>(CourseObjectiveDO.class).eq(CourseObjectiveDO::getCourseId,courseId))
                .stream().map(CourseObjectiveDO::getId).toArray(Long[]::new);
        // 新增矩阵数据
        if(targetIds.length > 0){
            List<ObjectiveModeDO> list = new ArrayList<>();
            for (Long targetId : targetIds) {
                if(isObjType){
                    list.add(new ObjectiveModeDO().setCourseId(courseId).setObjectiveId(id).setModeId(targetId).setScore(BigDecimal.valueOf(0)));
                }else{
                    list.add(new ObjectiveModeDO().setCourseId(courseId).setObjectiveId(targetId).setModeId(id).setScore(BigDecimal.valueOf(0)));
                }
            }
            if(!CollectionUtils.isEmpty(list)){
                objectiveModeMapper.insertBatch(list);
            }
        }
    }

    /**
     * 检查并移除考核评价方式矩阵数据
     * @param id 课程目标id/考核方式id
     * @param isObjType 是否课程目标
     */
    @Override
    public void checkWithRemoveEvaluateMode(Long id, boolean isObjType) {
        LambdaQueryWrapper<ObjectiveModeDO> wrapper = new LambdaQueryWrapper<>();
        if (isObjType) {
            wrapper.eq(ObjectiveModeDO::getObjectiveId, id);
        } else {
            wrapper.eq(ObjectiveModeDO::getModeId, id);
        }
        objectiveModeMapper.delete(wrapper);
    }

    /**
     * 查找所有矩阵数据
     * @return
     */
    @Override
    public List<ObjectiveModeExDO> listObjectiveMode(Long courseId) {
        return objectiveModeMapper.listObjectiveMode(courseId);
    }

    /**
     * 批量更新矩阵数据
     * @param updateReqVOs
     */
    @Override
    public void updateObjectiveModeList(List<ObjectiveModeSaveReqVO> updateReqVOs) {
        for (ObjectiveModeSaveReqVO updateReqVO : updateReqVOs) {
            // 校验存在
            validateObjectiveModeExists(updateReqVO.getId());
        }
        List<ObjectiveModeDO> updateList = updateReqVOs.stream()
                .map(req -> {
                    ObjectiveModeDO obj = new ObjectiveModeDO();
                    obj.setId(req.getId());
                    obj.setScore(req.getScore());
                    return obj;
                })
                .collect(Collectors.toList());
        //score 必须是 包装类,只更新非 null 字段
        objectiveModeMapper.updateById(updateList);
    }

}