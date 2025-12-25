package cn.fengp.basic.module.nmt.service.objectivemode;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 课程目标考核评价 Service 接口
 *
 * @author fengpeng
 */
public interface ObjectiveModeService {

    /**
     * 创建课程目标考核评价
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createObjectiveMode(@Valid ObjectiveModeSaveReqVO createReqVO);

    /**
     * 更新课程目标考核评价
     *
     * @param updateReqVO 更新信息
     */
    void updateObjectiveMode(@Valid ObjectiveModeSaveReqVO updateReqVO);

    /**
     * 删除课程目标考核评价
     *
     * @param id 编号
     */
    void deleteObjectiveMode(Long id);

    /**
    * 批量删除课程目标考核评价
    *
    * @param ids 编号
    */
    void deleteObjectiveModeListByIds(List<Long> ids);

    /**
     * 获得课程目标考核评价
     *
     * @param id 编号
     * @return 课程目标考核评价
     */
    ObjectiveModeDO getObjectiveMode(Long id);

    /**
     * 获得课程目标考核评价分页
     *
     * @param pageReqVO 分页查询
     * @return 课程目标考核评价分页
     */
    PageResult<ObjectiveModeDO> getObjectiveModePage(ObjectiveModePageReqVO pageReqVO);

}