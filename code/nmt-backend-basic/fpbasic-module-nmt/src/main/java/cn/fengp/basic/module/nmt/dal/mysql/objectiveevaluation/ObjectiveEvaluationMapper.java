package cn.fengp.basic.module.nmt.dal.mysql.objectiveevaluation;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation.ObjectiveEvaluationDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo.*;

/**
 * 课程目标达成度评价 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface ObjectiveEvaluationMapper extends BaseMapperX<ObjectiveEvaluationDO> {

    default PageResult<ObjectiveEvaluationDO> selectPage(ObjectiveEvaluationPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ObjectiveEvaluationDO>()
                .eqIfPresent(ObjectiveEvaluationDO::getEvaluationId, reqVO.getEvaluationId())
                .eqIfPresent(ObjectiveEvaluationDO::getObjectiveId, reqVO.getObjectiveId())
                .likeIfPresent(ObjectiveEvaluationDO::getObjectiveName, reqVO.getObjectiveName())
                .eqIfPresent(ObjectiveEvaluationDO::getObjectiveContent, reqVO.getObjectiveContent())
                .eqIfPresent(ObjectiveEvaluationDO::getComment, reqVO.getComment())
                .orderByDesc(ObjectiveEvaluationDO::getId));
    }

}