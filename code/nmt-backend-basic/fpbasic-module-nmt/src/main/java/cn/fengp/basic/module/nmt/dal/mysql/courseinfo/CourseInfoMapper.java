package cn.fengp.basic.module.nmt.dal.mysql.courseinfo;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.courseinfo.CourseInfoDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo.*;

/**
 * 课程信息 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface CourseInfoMapper extends BaseMapperX<CourseInfoDO> {

    default PageResult<CourseInfoDO> selectPage(CourseInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CourseInfoDO>()
                .eqIfPresent(CourseInfoDO::getNumber, reqVO.getNumber())
                .likeIfPresent(CourseInfoDO::getName, reqVO.getName())
                .eqIfPresent(CourseInfoDO::getMajorType, reqVO.getMajorType())
                .eqIfPresent(CourseInfoDO::getCourseType, reqVO.getCourseType())
                .eqIfPresent(CourseInfoDO::getCourseProperty, reqVO.getCourseProperty())
                .eqIfPresent(CourseInfoDO::getGrade, reqVO.getGrade())
                .eqIfPresent(CourseInfoDO::getTerm, reqVO.getTerm())
                .likeIfPresent(CourseInfoDO::getTeacherName, reqVO.getTeacherName())
                .betweenIfPresent(CourseInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(CourseInfoDO::getId));
    }

}