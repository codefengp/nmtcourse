package cn.fengp.basic.module.nmt.dal.mysql.evaluateplan;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.*;

/**
 * 课程考核计划 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface EvaluatePlanMapper extends BaseMapperX<EvaluatePlanDO> {

    default PageResult<EvaluatePlanDO> selectPage(EvaluatePlanPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EvaluatePlanDO>()
                .eqIfPresent(EvaluatePlanDO::getObjectiveModeId, reqVO.getObjectiveModeId())
                .eqIfPresent(EvaluatePlanDO::getObjectiveId, reqVO.getObjectiveId())
                .eqIfPresent(EvaluatePlanDO::getModeId, reqVO.getModeId())
                .eqIfPresent(EvaluatePlanDO::getScore, reqVO.getScore())
                .eqIfPresent(EvaluatePlanDO::getContent, reqVO.getContent())
                .orderByDesc(EvaluatePlanDO::getId));
    }

}