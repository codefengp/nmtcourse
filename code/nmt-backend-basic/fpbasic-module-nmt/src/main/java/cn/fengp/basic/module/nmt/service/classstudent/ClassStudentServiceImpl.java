package cn.fengp.basic.module.nmt.service.classstudent;

import cn.fengp.basic.framework.common.exception.ServiceException;
import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.framework.common.util.validation.ValidationUtils;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.fengp.basic.module.nmt.dal.dataobject.teachclass.TeachClassDO;
import cn.fengp.basic.module.nmt.dal.mysql.teachclass.TeachClassMapper;
import cn.fengp.basic.module.nmt.service.teachclass.TeachClassService;
import cn.fengp.basic.module.nmt.util.ExcelExtUtils;
import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.annotations.VisibleForTesting;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
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
    @Resource
    private TeachClassService teachClassService;

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
        //更新班级人数
        this.updateClassStudentTotalNumber(createReqVO.getClassId());
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
        ClassStudentDO classStudentDO = validateClassStudentExists(id);
        // 删除
        classStudentMapper.deleteById(id);
        //更新班级人数
        this.updateClassStudentTotalNumber(classStudentDO.getClassId());
    }

    @Override
    public void deleteClassStudentListByIds(List<Long> ids) {
        ClassStudentDO classStudentDO = classStudentMapper.selectById(ids.get(0));
        // 删除
        classStudentMapper.deleteByIds(ids);
        //更新班级人数
        this.updateClassStudentTotalNumber(classStudentDO.getClassId());
    }


    private ClassStudentDO validateClassStudentExists(Long id) {
        ClassStudentDO classStudentDO = classStudentMapper.selectById(id);
        if (classStudentDO == null) {
            throw exception(CLASS_STUDENT_NOT_EXISTS);
        }
        return classStudentDO;
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
    public List<ClassStudentDO> listClassStudent(Long classId) {
        return classStudentMapper.selectList(new LambdaQueryWrapperX<ClassStudentDO>()
                .eq(ClassStudentDO::getClassId, classId).orderByAsc(ClassStudentDO::getNumber));
    }

    @Override
    public void outFail(HttpServletResponse response, JSONObject params) throws IOException {
        List<JSONObject> data = new ArrayList<>();
        JSONArray successData = params.getJSONArray("successData");
        JSONArray failData = params.getJSONArray("failData");
        //转成List<JSONObject>-错误数据
        failData.forEach(obj -> data.add((JSONObject) obj));
        // 插入提示行
        JSONObject commentRow = new JSONObject();
        commentRow.put("number", "--错误数据分割行，下方是验证通过的数据行--");
        commentRow.put("name", "");
        commentRow.put("isComment", true);
        data.add(commentRow);
        //转成List<JSONObject>-正确数据
        successData.forEach(obj -> data.add((JSONObject) obj));
        ExcelExtUtils.writeWithComment(response, "错误信息.xls", "错误列表", ClassStudentImportExcelVO.class, data);

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
    public void importExcel(JSONObject params) {
        JSONObject bizParams = params.getJSONObject("bizParams");
        if(bizParams == null || !StringUtils.hasText(bizParams.getString("classId"))){
            throw ServiceExceptionUtil.invalidParamException("班级标识不能为空");
        }
        String classId = bizParams.getString("classId");
        JSONArray successData = params.getJSONArray("successData");
        if (CollectionUtils.isEmpty(successData)) {
            throw ServiceExceptionUtil.invalidParamException("导入数据不能为空");
        }
        List<ClassStudentSaveReqVO> data = new ArrayList<>();
        //转成List<JSONObject>
        successData.forEach(obj -> data.add(BeanUtils.toBean(obj,ClassStudentSaveReqVO.class)));
        List<ClassStudentDO> studentDOS = successData.stream()
                .map(stu -> {
                    ClassStudentDO dto = BeanUtils.toBean(stu, ClassStudentDO.class);
                    dto.setClassId(Long.parseLong(classId));
                    return dto;
                })
                .collect(Collectors.toList());
        classStudentMapper.insertBatch(studentDOS);
        //更新班级人数
        if(Objects.nonNull(studentDOS.get(0))){
            this.updateClassStudentTotalNumber(studentDOS.get(0).getClassId());
        }
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

    /**
     * 更新班级人数
     * @param classId
     */
    private void updateClassStudentTotalNumber(Long classId){
        if (Objects.isNull(classId)) {
            throw ServiceExceptionUtil.invalidParamException("班级标识为空");
        }
        LambdaQueryWrapperX<ClassStudentDO> wrapperX = new LambdaQueryWrapperX<ClassStudentDO>().eq(ClassStudentDO::getClassId, classId);
        int count = classStudentMapper.selectCount(wrapperX).intValue();
        teachClassService.updateTotalNumber(classId,count);
    }

}