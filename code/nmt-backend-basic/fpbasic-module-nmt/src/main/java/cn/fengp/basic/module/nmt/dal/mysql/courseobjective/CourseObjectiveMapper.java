package cn.fengp.basic.module.nmt.dal.mysql.courseobjective;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.*;

/**
 * 课程目标 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface CourseObjectiveMapper extends BaseMapperX<CourseObjectiveDO> {

    default PageResult<CourseObjectiveDO> selectPage(CourseObjectivePageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CourseObjectiveDO>()
                .likeIfPresent(CourseObjectiveDO::getName, reqVO.getName())
                .eqIfPresent(CourseObjectiveDO::getContent, reqVO.getContent())
                .eqIfPresent(CourseObjectiveDO::getExpectValue, reqVO.getExpectValue())
                .eqIfPresent(CourseObjectiveDO::getCourseId, reqVO.getCourseId())
                .orderByAsc(CourseObjectiveDO::getCreateTime));
    }

}