package cn.fengp.basic.module.nmt.controller.admin.courseobjective;

import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import jakarta.validation.constraints.*;
import jakarta.validation.*;
import jakarta.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

import cn.fengp.basic.framework.excel.core.util.ExcelUtils;

import cn.fengp.basic.framework.apilog.core.annotation.ApiAccessLog;
import static cn.fengp.basic.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.module.nmt.service.courseobjective.CourseObjectiveService;

@Tag(name = "管理后台 - 课程目标")
@RestController
@RequestMapping("/nmt/course-objective")
@Validated
public class CourseObjectiveController {

    @Resource
    private CourseObjectiveService courseObjectiveService;

    @PostMapping("/create")
    @Operation(summary = "创建课程目标")
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:create')")
    public CommonResult<Long> createCourseObjective(@Valid @RequestBody CourseObjectiveSaveReqVO createReqVO) {
        return success(courseObjectiveService.createCourseObjective(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程目标")
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:update')")
    public CommonResult<Boolean> updateCourseObjective(@Valid @RequestBody CourseObjectiveSaveReqVO updateReqVO) {
        courseObjectiveService.updateCourseObjective(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程目标")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:delete')")
    public CommonResult<Boolean> deleteCourseObjective(@RequestParam("id") Long id) {
        courseObjectiveService.deleteCourseObjective(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程目标")
                @PreAuthorize("@ss.hasPermission('nmt:course-objective:delete')")
    public CommonResult<Boolean> deleteCourseObjectiveList(@RequestParam("ids") List<Long> ids) {
        courseObjectiveService.deleteCourseObjectiveListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程目标")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:query')")
    public CommonResult<CourseObjectiveRespVO> getCourseObjective(@RequestParam("id") Long id) {
        CourseObjectiveDO courseObjective = courseObjectiveService.getCourseObjective(id);
        return success(BeanUtils.toBean(courseObjective, CourseObjectiveRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程目标分页")
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:query')")
    public CommonResult<PageResult<CourseObjectiveRespVO>> getCourseObjectivePage(@Valid CourseObjectivePageReqVO pageReqVO) {
        PageResult<CourseObjectiveDO> pageResult = courseObjectiveService.getCourseObjectivePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CourseObjectiveRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程目标 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:course-objective:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseObjectiveExcel(@Valid CourseObjectivePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CourseObjectiveDO> list = courseObjectiveService.getCourseObjectivePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程目标.xls", "数据", CourseObjectiveRespVO.class,
                        BeanUtils.toBean(list, CourseObjectiveRespVO.class));
    }

}