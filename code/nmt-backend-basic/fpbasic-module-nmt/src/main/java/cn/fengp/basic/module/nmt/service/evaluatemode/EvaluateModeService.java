package cn.fengp.basic.module.nmt.service.evaluatemode;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 考核评价方式 Service 接口
 *
 * @author fengpeng
 */
public interface EvaluateModeService {

    /**
     * 创建考核评价方式
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createEvaluateMode(@Valid EvaluateModeSaveReqVO createReqVO);

    /**
     * 更新考核评价方式
     *
     * @param updateReqVO 更新信息
     */
    void updateEvaluateMode(@Valid EvaluateModeSaveReqVO updateReqVO);

    /**
     * 删除考核评价方式
     *
     * @param id 编号
     */
    void deleteEvaluateMode(Long id);

    /**
    * 批量删除考核评价方式
    *
    * @param ids 编号
    */
    void deleteEvaluateModeListByIds(List<Long> ids);

    /**
     * 获得考核评价方式
     *
     * @param id 编号
     * @return 考核评价方式
     */
    EvaluateModeDO getEvaluateMode(Long id);

    /**
     * 获得考核评价方式分页
     *
     * @param pageReqVO 分页查询
     * @return 考核评价方式分页
     */
    PageResult<EvaluateModeDO> getEvaluateModePage(EvaluateModePageReqVO pageReqVO);

}