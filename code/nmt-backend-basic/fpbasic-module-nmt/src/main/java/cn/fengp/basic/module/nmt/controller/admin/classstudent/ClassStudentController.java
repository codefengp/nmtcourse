package cn.fengp.basic.module.nmt.controller.admin.classstudent;

import cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil;
import cn.fengp.basic.module.nmt.controller.admin.common.AbstractImportController;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.fengp.basic.module.nmt.util.ExcelExtUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

import cn.fengp.basic.framework.excel.core.util.ExcelUtils;

import cn.fengp.basic.module.nmt.controller.admin.classstudent.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentDO;
import cn.fengp.basic.module.nmt.service.classstudent.ClassStudentService;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理后台 - 班级学生")
@RestController
@RequestMapping("/nmt/class-student")
@Validated
public class ClassStudentController extends AbstractImportController<ClassStudentImportExcelVO> {

    @Resource
    private ClassStudentService classStudentService;

    @PostMapping("/create")
    @Operation(summary = "创建班级学生")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    public CommonResult<Long> createClassStudent(@Valid @RequestBody ClassStudentSaveReqVO createReqVO) {
        return success(classStudentService.createClassStudent(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新班级学生")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:update')")
    public CommonResult<Boolean> updateClassStudent(@Valid @RequestBody ClassStudentSaveReqVO updateReqVO) {
        classStudentService.updateClassStudent(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除班级学生")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:class-student:delete')")
    public CommonResult<Boolean> deleteClassStudent(@RequestParam("id") Long id) {
        classStudentService.deleteClassStudent(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除班级学生")
                @PreAuthorize("@ss.hasPermission('nmt:class-student:delete')")
    public CommonResult<Boolean> deleteClassStudentList(@RequestParam("ids") List<Long> ids) {
        classStudentService.deleteClassStudentListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得班级学生")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:query')")
    public CommonResult<ClassStudentRespVO> getClassStudent(@RequestParam("id") Long id) {
        ClassStudentDO classStudent = classStudentService.getClassStudent(id);
        return success(BeanUtils.toBean(classStudent, ClassStudentRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得班级学生分页")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:query')")
    public CommonResult<PageResult<ClassStudentRespExVO>> getClassStudentPage(@Valid ClassStudentPageReqVO pageReqVO) {
        PageResult<ClassStudentExDO> pageResult = classStudentService.getClassStudentPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ClassStudentRespExVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得班级学生")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:query')")
    public CommonResult<List<ClassStudentRespVO>> listClassStudent(@RequestParam("classId") Long classId) {
        List<ClassStudentDO> students = classStudentService.listClassStudent(classId);
        return success(BeanUtils.toBean(students, ClassStudentRespVO.class));
    }

/*    @GetMapping("/export-excel")
    @Operation(summary = "导出班级学生 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportClassStudentExcel(@Valid ClassStudentPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ClassStudentDO> list = classStudentService.getClassStudentPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "班级学生.xls", "数据", ClassStudentRespVO.class,
                        BeanUtils.toBean(list, ClassStudentRespVO.class));
    }*/

    @PostMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    @Override
    public void downloadTemplate(HttpServletResponse response, String bodyParams) throws Exception {
        //解析参数
        JSONObject bizParams = parseBizParams(bodyParams);
        ExcelUtils.write(response, "学生导入模板.xls", "学生列表", ClassStudentImportExcelVO.class, new ArrayList<>());
    }

    @PostMapping("/import-validate")
    @Operation(summary = "验证导入")
    @Parameters({@Parameter(name = "file", description = "Excel 文件", required = true)})
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    @Override
    public CommonResult<JSONObject> validateImport(@RequestParam("file") MultipartFile file,@RequestParam(value = "bodyParams", required = false) String bodyParams) throws Exception {
        //解析参数
        JSONObject bizParams = parseBizParams(bodyParams);
        List<ClassStudentImportExcelVO> list = ExcelUtils.read(file, ClassStudentImportExcelVO.class);
        return success(classStudentService.validateImport(list));
    }

    @PostMapping("/out-fail")
    @Operation(summary = "导出错误")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    @Override
    public void outFail(HttpServletResponse response,@RequestParam(value = "bodyParams", required = false) String bodyParams) throws Exception {
        JSONObject bizParams = parseBizParams(bodyParams);
        List<JSONObject> data = new ArrayList<>();
        String successData = bizParams.getString("successData");
        String failData = bizParams.getString("failData");
        //转成List<JSONObject>-错误数据
        JSONArray.parseArray(failData).forEach(obj -> data.add((JSONObject) obj));
        // 插入提示行
        JSONObject commentRow = new JSONObject();
        commentRow.put("number", "--以下是验证通过的数据，导入前请删除--");
        commentRow.put("name", "");
        commentRow.put("isComment", true);
        data.add(commentRow);
        //转成List<JSONObject>-正确数据
        JSONArray.parseArray(successData).forEach(obj -> data.add((JSONObject) obj));
        ExcelExtUtils.writeWithComment(response, "错误信息.xls", "错误列表", ClassStudentImportExcelVO.class, data);
    }

    @PostMapping("/import")
    @Operation(summary = "导入")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    @Override
    public CommonResult<Boolean> importExcel(@RequestBody String bodyParams) throws Exception {
        JSONObject bizParams = parseBizParams(bodyParams);
        String classId = bizParams.getString("classId");
        String successData = bizParams.getString("successData");
        if (!StringUtils.hasText(classId) || !StringUtils.hasText(successData)) {
            throw ServiceExceptionUtil.invalidParamException("参数不能为空");
        }
        List<ClassStudentSaveReqVO> data = new ArrayList<>();
        //转成List<JSONObject>
        JSONArray.parseArray(successData).forEach(obj -> data.add(BeanUtils.toBean(obj,ClassStudentSaveReqVO.class)));
        classStudentService.importExcel(data,classId);
        return success(true);
    }

}