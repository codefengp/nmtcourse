package cn.fengp.basic.module.nmt.controller.admin.classstudent;

import cn.fengp.basic.module.nmt.dal.dataobject.classstudent.ClassStudentExDO;
import cn.fengp.basic.module.nmt.util.ExcelExtUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Parameters;
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
public class ClassStudentController {

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

    @GetMapping("/get-import-template")
    @Operation(summary = "获得导入模板")
    public void importTemplate(HttpServletResponse response) throws IOException {
        // 输出
        ExcelUtils.write(response, "学生导入模板.xls", "学生列表", ClassStudentImportExcelVO.class, new ArrayList<ClassStudentImportExcelVO>());
    }

    @PostMapping("/import-validate")
    @Operation(summary = "验证导入")
    @Parameters({@Parameter(name = "file", description = "Excel 文件", required = true)})
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    public CommonResult<JSONObject> validateImport(@RequestParam("file") MultipartFile file) throws Exception {
        List<ClassStudentImportExcelVO> list = ExcelUtils.read(file, ClassStudentImportExcelVO.class);
        return success(classStudentService.validateImport(list));
    }

    @PostMapping("/out-fail")
    @Operation(summary = "导出错误")
    @PreAuthorize("@ss.hasPermission('nmt:class-student:create')")
    public void outFail(HttpServletResponse response,@RequestBody String tableData) throws Exception {
        List<JSONObject> data = new ArrayList<>();
        JSONObject params = JSONObject.parseObject(tableData);
        String successData = params.getString("successData");
        String failData = params.getString("failData");
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
    public CommonResult<Boolean> importExcel(@RequestBody String successData) throws Exception {
        List<ClassStudentSaveReqVO> data = new ArrayList<>();
        //转成List<JSONObject>
        JSONArray.parseArray(successData).forEach(obj -> data.add(BeanUtils.toBean(obj,ClassStudentSaveReqVO.class)));
        classStudentService.importExcel(data);
        return success(true);
    }

}