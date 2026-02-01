package cn.fengp.basic.module.nmt.dal.mysql.achievementevaluation;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.achievementevaluation.AchievementEvaluationDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo.*;

/**
 * 达成度评价 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface AchievementEvaluationMapper extends BaseMapperX<AchievementEvaluationDO> {

    default PageResult<AchievementEvaluationDO> selectPage(AchievementEvaluationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AchievementEvaluationDO>()
                .eqIfPresent(AchievementEvaluationDO::getCourseId, reqVO.getCourseId())
                .eqIfPresent(AchievementEvaluationDO::getClassId, reqVO.getClassId())
                .eqIfPresent(AchievementEvaluationDO::getOverallComment, reqVO.getOverallComment())
                .eqIfPresent(AchievementEvaluationDO::getProblemAnalysis, reqVO.getProblemAnalysis())
                .eqIfPresent(AchievementEvaluationDO::getImprovementPlan, reqVO.getImprovementPlan())
                .orderByDesc(AchievementEvaluationDO::getId));
    }

}