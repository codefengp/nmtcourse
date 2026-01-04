package cn.fengp.basic.module.nmt.service.evaluateplan;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 课程考核计划 Service 接口
 *
 * @author fengpeng
 */
public interface EvaluatePlanService {

    /**
     * 创建课程考核计划
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEvaluatePlan(@Valid EvaluatePlanSaveReqVO createReqVO);

    /**
     * 更新课程考核计划
     *
     * @param updateReqVO 更新信息
     */
    void updateEvaluatePlan(@Valid EvaluatePlanSaveReqVO updateReqVO);

    /**
     * 删除课程考核计划
     *
     * @param id 编号
     */
    void deleteEvaluatePlan(Long id);

    /**
    * 批量删除课程考核计划
    *
    * @param ids 编号
    */
    void deleteEvaluatePlanListByIds(List<Long> ids);

    /**
     * 获得课程考核计划
     *
     * @param id 编号
     * @return 课程考核计划
     */
    EvaluatePlanDO getEvaluatePlan(Long id);

    /**
     * 获得课程考核计划分页
     *
     * @param pageReqVO 分页查询
     * @return 课程考核计划分页
     */
    PageResult<EvaluatePlanDO> getEvaluatePlanPage(EvaluatePlanPageReqVO pageReqVO);

}