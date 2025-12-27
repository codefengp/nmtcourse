package cn.fengp.basic.module.nmt.dal.mysql.objectivemode;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeDO;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeExDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.*;

/**
 * 课程目标考核评价 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface ObjectiveModeMapper extends BaseMapperX<ObjectiveModeDO> {

    default PageResult<ObjectiveModeDO> selectPage(ObjectiveModePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ObjectiveModeDO>()
                .eqIfPresent(ObjectiveModeDO::getObjectiveId, reqVO.getObjectiveId())
                .eqIfPresent(ObjectiveModeDO::getModeId, reqVO.getModeId())
                .eqIfPresent(ObjectiveModeDO::getScore, reqVO.getScore())
                .orderByDesc(ObjectiveModeDO::getId));
    }


    List<ObjectiveModeExDO> listObjectiveMode();

}