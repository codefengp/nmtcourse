package cn.fengp.basic.module.nmt.dal.mysql.studentachievement;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.*;
import org.apache.ibatis.annotations.Param;

/**
 * 学生成绩 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface StudentAchievementMapper extends BaseMapperX<StudentAchievementDO> {

    default PageResult<StudentAchievementDO> selectPage(StudentAchievementPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<StudentAchievementDO>()
                .eqIfPresent(StudentAchievementDO::getStudentId, reqVO.getStudentId())
                .eqIfPresent(StudentAchievementDO::getPlanId, reqVO.getPlanId())
                .eqIfPresent(StudentAchievementDO::getObjectiveId, reqVO.getObjectiveId())
                .eqIfPresent(StudentAchievementDO::getModeId, reqVO.getModeId())
                .eqIfPresent(StudentAchievementDO::getScore, reqVO.getScore())
                .orderByDesc(StudentAchievementDO::getId));
    }

    List<StudentAchievementDO> listStudentAchievement(@Param("classId") Long classId);
    List<StudentAchievementPlanDO> listStudentWithAchievement(@Param("classId") Long classId);
}