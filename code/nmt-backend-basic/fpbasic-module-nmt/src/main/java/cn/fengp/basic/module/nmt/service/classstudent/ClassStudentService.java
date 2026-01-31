package cn.fengp.basic.module.nmt.service.classstudent;

import java.io.IOException;
import java.util.*;

import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.classstudent.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.framework.common.pojo.PageResult;

/**
 * 班级学生 Service 接口
 *
 * @author fengpeng
 */
public interface ClassStudentService {

    /**
     * 创建班级学生
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createClassStudent(@Valid ClassStudentSaveReqVO createReqVO);

    /**
     * 更新班级学生
     *
     * @param updateReqVO 更新信息
     */
    void updateClassStudent(@Valid ClassStudentSaveReqVO updateReqVO);

    /**
     * 删除班级学生
     *
     * @param id 编号
     */
    void deleteClassStudent(Long id);

    /**
    * 批量删除班级学生
    *
    * @param ids 编号
    */
    void deleteClassStudentListByIds(List<Long> ids);

    /**
     * 获得班级学生
     *
     * @param id 编号
     * @return 班级学生
     */
    ClassStudentDO getClassStudent(Long id);

    /**
     * 获得班级学生分页
     *
     * @param pageReqVO 分页查询
     * @return 班级学生分页
     */
    PageResult<ClassStudentExDO> getClassStudentPage(ClassStudentPageReqVO pageReqVO);

    /**
     * 验证导入数据
     * @param list
     * @return
     */
    JSONObject validateImport(List<ClassStudentImportExcelVO> list);

    /**
     * 导入数据
     * @return
     */
    void importExcel(JSONObject params);

    List<ClassStudentDO> listClassStudent(Long classId);

    void outFail(HttpServletResponse response, JSONObject params) throws IOException;
}