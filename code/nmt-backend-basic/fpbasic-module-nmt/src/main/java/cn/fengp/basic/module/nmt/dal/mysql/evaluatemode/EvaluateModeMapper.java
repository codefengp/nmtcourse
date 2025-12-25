package cn.fengp.basic.module.nmt.dal.mysql.evaluatemode;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.*;

/**
 * 考核评价方式 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface EvaluateModeMapper extends BaseMapperX<EvaluateModeDO> {

    default PageResult<EvaluateModeDO> selectPage(EvaluateModePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<EvaluateModeDO>()
                .likeIfPresent(EvaluateModeDO::getName, reqVO.getName())
                .eqIfPresent(EvaluateModeDO::getWeight, reqVO.getWeight())
                .eqIfPresent(EvaluateModeDO::getCourseId, reqVO.getCourseId())
                .orderByAsc(EvaluateModeDO::getCreateTime));
    }

}