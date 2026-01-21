package cn.fengp.basic.module.nmt.service.classstudent;

import cn.fengp.basic.framework.common.exception.ServiceException;
import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.common.util.validation.ValidationUtils;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.annotations.VisibleForTesting;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;

import cn.fengp.basic.module.nmt.controller.admin.classstudent.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.classstudent.ClassStudentMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
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

    @Override
    public JSONObject validateImport(List<ClassStudentImportExcelVO> importDataList) {
        List<JSONObject> successData = new ArrayList<>();//验证成功数据
        List<JSONObject> failData = new ArrayList<>();//验证失败数据
        List<ClassStudentImportExcelVO> existImportData = new ArrayList<>();//表格中已验证过数据
        // 1 参数校验
        if (CollUtil.isEmpty(importDataList)) {
            throw exception(IMPORT_LIST_IS_EMPTY);
        }
        // 2 遍历，逐个数据校验
        for (ClassStudentImportExcelVO importData : importDataList) {
            boolean validateFlag = true;//校验标识
            JSONObject obj = BeanUtils.toBean(importData, JSONObject.class);
            // 2.1 校验字段是否符合要求 -- 通过vo注解校验
            try {
                //根据vo的注解校验
                ValidationUtils.validate(BeanUtils.toBean(importData, ClassStudentImportSaveVO.class));
            } catch (ConstraintViolationException ex){
                for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                    String fieldName = violation.getPropertyPath().toString();
                    String message = violation.getMessage();
                    obj.put(fieldName +"__error", message);//将错误信息放入json中
                }
                validateFlag = false;//校验失败
            }
            // 2.2 校验，判断是否有不符合的原因 --- 根据业务校验
            validateFlag = validateBusiness(importData,obj,existImportData) && validateFlag;
            if (validateFlag){
                //将成功信息放入successData中
                successData.add(obj);
            }else {
                //将错误信息放入failData中
                failData.add(obj);
            }
            //将数据放入existImportData中，进行表格中数据验重
            existImportData.add(importData);
        }
        // 3. 返回结果
        JSONObject result = new JSONObject();
        result.put("successData",successData);
        result.put("failData",failData);
        return result;
    }

    @Override
    public void importExcel(List<ClassStudentSaveReqVO> list) {

    }

    /**
     * 根据业务校验
     * @param vo
     * @param obj
     * @return
     */
    private Boolean validateBusiness(ClassStudentImportExcelVO vo,JSONObject obj,List<ClassStudentImportExcelVO> existImportData) {
        // 校验学号唯一
        return validateNumberUnique(vo.getNumber(), obj,existImportData);
    }

    /**
     * 校验学号存在
     * @param number
     */
    boolean validateNumberUnique(String number,JSONObject obj,List<ClassStudentImportExcelVO> existImportData) {
        if(Objects.nonNull(obj.get("number__error"))){//已有错误信息(之前验证非空)
            return false;
        }
        //验证表格中学号重复
        Optional<ClassStudentImportExcelVO> first = existImportData.stream()
                .filter(cs -> StringUtils.hasText(cs.getNumber()) && cs.getNumber().equals(number))
                .findFirst();
        if(first.isPresent()){
            obj.put("number__error",CLASS_STUDENT_TABlE_NUMBER_EXISTS.getMsg());
            return false;
        }
        //验证数据库中学号重复
        ClassStudentDO classStudentDO = classStudentMapper.selectOne(new LambdaQueryWrapper<ClassStudentDO>().eq(ClassStudentDO::getNumber, number));
        if (Objects.nonNull(classStudentDO)) {
            obj.put("number__error",CLASS_STUDENT_NUMBER_EXISTS.getMsg());
            return false;
        }
        return true;
    }

}