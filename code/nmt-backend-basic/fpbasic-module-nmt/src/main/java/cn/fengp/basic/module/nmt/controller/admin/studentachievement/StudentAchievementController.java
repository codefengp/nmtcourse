package cn.fengp.basic.module.nmt.controller.admin.studentachievement;

import cn.fengp.basic.framework.apilog.core.annotation.ApiAccessLog;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.framework.excel.core.util.ExcelUtils;
import cn.fengp.basic.module.nmt.controller.admin.common.AbstractImportController;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.StudentAchievementPageReqVO;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.StudentAchievementRespVO;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.StudentAchievementSaveReqVO;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.module.nmt.service.studentachievement.StudentAchievementService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static cn.fengp.basic.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 学生成绩")
@RestController
@RequestMapping("/nmt/student-achievement")
@Validated
public class StudentAchievementController extends AbstractImportController {

    @Resource
    private StudentAchievementService studentAchievementService;

    @PostMapping("/create")
    @Operation(summary = "创建学生成绩")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:create')")
    public CommonResult<Long> createStudentAchievement(@Valid @RequestBody StudentAchievementSaveReqVO createReqVO) {
        return success(studentAchievementService.createStudentAchievement(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新学生成绩")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:update')")
    public CommonResult<Boolean> updateStudentAchievement(@Valid @RequestBody StudentAchievementSaveReqVO updateReqVO) {
        studentAchievementService.updateStudentAchievement(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除学生成绩")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:delete')")
    public CommonResult<Boolean> deleteStudentAchievement(@RequestParam("id") Long id) {
        studentAchievementService.deleteStudentAchievement(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除学生成绩")
                @PreAuthorize("@ss.hasPermission('nmt:student-achievement:delete')")
    public CommonResult<Boolean> deleteStudentAchievementList(@RequestParam("ids") List<Long> ids) {
        studentAchievementService.deleteStudentAchievementListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得学生成绩")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:query')")
    public CommonResult<StudentAchievementRespVO> getStudentAchievement(@RequestParam("id") Long id) {
        StudentAchievementDO studentAchievement = studentAchievementService.getStudentAchievement(id);
        return success(BeanUtils.toBean(studentAchievement, StudentAchievementRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得学生成绩分页")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:query')")
    public CommonResult<PageResult<StudentAchievementRespVO>> getStudentAchievementPage(@Valid StudentAchievementPageReqVO pageReqVO) {
        PageResult<StudentAchievementDO> pageResult = studentAchievementService.getStudentAchievementPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, StudentAchievementRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出学生成绩 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportStudentAchievementExcel(@Valid StudentAchievementPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<StudentAchievementDO> list = studentAchievementService.getStudentAchievementPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "学生成绩.xls", "数据", StudentAchievementRespVO.class,
                        BeanUtils.toBean(list, StudentAchievementRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得学生成绩数据")
    @PreAuthorize("@ss.hasPermission('nmt:student-achievement:query')")
    public CommonResult<List<StudentAchievementRespVO>> listStudentAchievement(@RequestParam("classId") Long classId) {
        List<StudentAchievementDO> studentAchievementDOs = studentAchievementService.listStudentAchievement(classId);
        return success(BeanUtils.toBean(studentAchievementDOs, StudentAchievementRespVO.class));
    }

    @PostMapping("/download-template")
    @Operation(summary = "获得导入模板")
    @PreAuthorize("@ss.hasPermission('nmt:class-achievement:create')")
    @Override
    public void downloadTemplate(HttpServletResponse response, @RequestBody String bodyParams) throws Exception {
        //解析参数
        JSONObject params = super.parseBodyParams(bodyParams);
        studentAchievementService.downloadTemplate(response, params);
    }

    @Override
    public CommonResult<JSONObject> validateImport(MultipartFile file, String bodyParams) throws Exception {
        return null;
    }

    @Override
    public void outFail(HttpServletResponse response, String bodyParams) throws Exception {

    }

    @Override
    public CommonResult<Boolean> importExcelData(String bodyParams) throws Exception {
        return null;
    }
}