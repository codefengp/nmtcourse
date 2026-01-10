package cn.fengp.basic.module.nmt.service.classstudent;

import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.classstudent.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.classstudent.ClassStudentMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 班级学生 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class ClassStudentServiceImpl implements ClassStudentService {

    @Resource
    private ClassStudentMapper classStudentMapper;

    @Override
    public Long createClassStudent(ClassStudentSaveReqVO createReqVO) {
        // 插入
        ClassStudentDO classStudent = BeanUtils.toBean(createReqVO, ClassStudentDO.class);
        classStudentMapper.insert(classStudent);

        // 返回
        return classStudent.getId();
    }

    @Override
    public void updateClassStudent(ClassStudentSaveReqVO updateReqVO) {
        // 校验存在
        validateClassStudentExists(updateReqVO.getId());
        // 更新
        ClassStudentDO updateObj = BeanUtils.toBean(updateReqVO, ClassStudentDO.class);
        classStudentMapper.updateById(updateObj);
    }

    @Override
    public void deleteClassStudent(Long id) {
        // 校验存在
        validateClassStudentExists(id);
        // 删除
        classStudentMapper.deleteById(id);
    }

    @Override
        public void deleteClassStudentListByIds(List<Long> ids) {
        // 删除
        classStudentMapper.deleteByIds(ids);
        }


    private void validateClassStudentExists(Long id) {
        if (classStudentMapper.selectById(id) == null) {
            throw exception(CLASS_STUDENT_NOT_EXISTS);
        }
    }

    @Override
    public ClassStudentDO getClassStudent(Long id) {
        return classStudentMapper.selectById(id);
    }

    @Override
    public PageResult<ClassStudentExDO> getClassStudentPage(ClassStudentPageReqVO pageReqVO) {
        return classStudentMapper.selectPageEx(pageReqVO);
    }

}