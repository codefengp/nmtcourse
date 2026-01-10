package cn.fengp.basic.module.nmt.dal.mysql.classstudent;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeExDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.nmt.controller.admin.classstudent.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 班级学生 Mapper
 *
 * @author fengpeng
 */
@Mapper
public interface ClassStudentMapper extends BaseMapperX<ClassStudentDO> {

    default PageResult<ClassStudentDO> selectPage(ClassStudentPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ClassStudentDO>()
                .likeIfPresent(ClassStudentDO::getName, reqVO.getName())
                .eqIfPresent(ClassStudentDO::getNumber, reqVO.getNumber())
                .eqIfPresent(ClassStudentDO::getClassId, reqVO.getClassId())
                .orderByDesc(ClassStudentDO::getId));
    }

    PageResult<ClassStudentExDO> selectPageEx(@Param("reqVO") ClassStudentPageReqVO reqVO);

}