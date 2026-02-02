package cn.fengp.basic.module.nmt.service.achievementevaluation;

import java.util.*;

import com.alibaba.fastjson.JSONObject;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.achievementevaluation.AchievementEvaluationDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 达成度评价 Service 接口
 *
 * @author fengpeng
 */
public interface AchievementEvaluationService {

    /**
     * 创建达成度评价
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAchievementEvaluation(@Valid AchievementEvaluationSaveReqVO createReqVO);

    /**
     * 更新达成度评价
     *
     * @param updateReqVO 更新信息
     */
    void updateAchievementEvaluation(@Valid AchievementEvaluationSaveReqVO updateReqVO);

    /**
     * 删除达成度评价
     *
     * @param id 编号
     */
    void deleteAchievementEvaluation(Long id);

    /**
    * 批量删除达成度评价
    *
    * @param ids 编号
    */
    void deleteAchievementEvaluationListByIds(List<Long> ids);

    /**
     * 获得达成度评价
     *
     * @param id 编号
     * @return 达成度评价
     */
    AchievementEvaluationDO getAchievementEvaluation(Long id);

    /**
     * 获得达成度评价分页
     *
     * @param pageReqVO 分页查询
     * @return 达成度评价分页
     */
    PageResult<AchievementEvaluationDO> getAchievementEvaluationPage(AchievementEvaluationPageReqVO pageReqVO);

    JSONObject getCourseOverallScore(Long courseId, Long classId);
}