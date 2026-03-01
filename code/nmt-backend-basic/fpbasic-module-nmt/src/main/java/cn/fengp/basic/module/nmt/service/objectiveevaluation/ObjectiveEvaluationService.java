package cn.fengp.basic.module.nmt.service.objectiveevaluation;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation.ObjectiveEvaluationDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 课程目标达成度评价 Service 接口
 *
 * @author fengpeng
 */
public interface ObjectiveEvaluationService {

    /**
     * 创建课程目标达成度评价
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createObjectiveEvaluation(@Valid ObjectiveEvaluationSaveReqVO createReqVO);

    /**
     * 更新课程目标达成度评价
     *
     * @param updateReqVO 更新信息
     */
    void updateObjectiveEvaluation(@Valid ObjectiveEvaluationSaveReqVO updateReqVO);

    /**
     * 删除课程目标达成度评价
     *
     * @param id 编号
     */
    void deleteObjectiveEvaluation(Long id);

    /**
    * 批量删除课程目标达成度评价
    *
    * @param ids 编号
    */
    void deleteObjectiveEvaluationListByIds(List<Long> ids);

    /**
     * 获得课程目标达成度评价
     *
     * @param id 编号
     * @return 课程目标达成度评价
     */
    ObjectiveEvaluationDO getObjectiveEvaluation(Long id);

    /**
     * 获得课程目标达成度评价分页
     *
     * @param pageReqVO 分页查询
     * @return 课程目标达成度评价分页
     */
    PageResult<ObjectiveEvaluationDO> getObjectiveEvaluationPage(ObjectiveEvaluationPageReqVO pageReqVO);

    /**
     * 批量创建/更新课程目标达成度评价
     *
     * @param list 批量创建信息
     */
    int saveOrUpdateObjectiveEvaluationBatch(List<ObjectiveEvaluationDO> list);

    /**
     * 根据父id达成度评价获取课程目标达成度评价
     * @param id 父id达成度评价
     * @return 课程目标达成度评价
     */
    List<ObjectiveEvaluationDO> getByAchievementEvaluation(Long id);

}