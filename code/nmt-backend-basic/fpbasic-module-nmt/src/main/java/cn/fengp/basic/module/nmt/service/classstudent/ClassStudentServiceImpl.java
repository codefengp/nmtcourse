package cn.fengp.basic.module.nmt.service.classstudent;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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
        if(!StringUtils.hasText(createReqVO.getNumber())){
            throw ServiceExceptionUtil.invalidParamException("学号不能为空");
        }
        // 校验学生编号是否存在
        this.validateStudentNumberExist(null, createReqVO.getNumber());
        // 插入
        ClassStudentDO classStudent = BeanUtils.toBean(createReqVO, ClassStudentDO.class);
        classStudentMapper.insert(classStudent);

        // 返回
        return classStudent.getId();
    }

    @Override
    public void updateClassStudent(ClassStudentSaveReqVO updateReqVO) {
        if(!StringUtils.hasText(updateReqVO.getNumber())){
            throw ServiceExceptionUtil.invalidParamException("学号不能为空");
        }
        // 校验存在
        validateClassStudentExists(updateReqVO.getId());
        // 校验学生编号是否存在
        this.validateStudentNumberExist(updateReqVO.getId(), updateReqVO.getNumber());
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

    /**
     * 校验学生编号是否存在
     * @param id
     * @param number
     */
    private void validateStudentNumberExist(Long id,String number) {
        List<ClassStudentDO> classStudentDOS = classStudentMapper.selectList();
        List<String> numberList = classStudentDOS.stream()
                // 新增:查询所有number,编辑：排除自己的number
                .filter(cs -> id == null || !id.equals(cs.getId()))
                // 只取 number 字段
                .map(ClassStudentDO::getNumber)
                .collect(Collectors.toList());

        // 这里你可以继续判断 number 是否已存在
        if (numberList.contains(number)) {
            throw ServiceExceptionUtil.invalidParamException("学号已存在");
        }
    }

    @Override
    public ClassStudentDO getClassStudent(Long id) {
        return classStudentMapper.selectById(id);
    }

    @Override
    public PageResult<ClassStudentExDO> getClassStudentPage(ClassStudentPageReqVO pageReqVO) {
        // 必须使用 MyBatis Plus 的分页对象
        IPage<ClassStudentExDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        classStudentMapper.selectPageEx(page, pageReqVO);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

}