package cn.fengp.basic.module.nmt.dal.mysql.teachclass;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.teachclass.TeachClassDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.teachclass.vo.*;

/**
 * 教学班级 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface TeachClassMapper extends BaseMapperX<TeachClassDO> {

    default PageResult<TeachClassDO> selectPage(TeachClassPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<TeachClassDO>()
                .eqIfPresent(TeachClassDO::getNumber, reqVO.getNumber())
                .likeIfPresent(TeachClassDO::getName, reqVO.getName())
                .eqIfPresent(TeachClassDO::getTotalNumber, reqVO.getTotalNumber())
                .likeIfPresent(TeachClassDO::getTeacherName, reqVO.getTeacherName())
                .orderByDesc(TeachClassDO::getId));
    }

}