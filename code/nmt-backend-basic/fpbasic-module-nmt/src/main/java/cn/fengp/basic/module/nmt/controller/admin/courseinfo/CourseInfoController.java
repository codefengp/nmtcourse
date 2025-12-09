package cn.fengp.basic.module.nmt.controller.admin.courseinfo;

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

import cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseinfo.CourseInfoDO;
import cn.fengp.basic.module.nmt.service.courseinfo.CourseInfoService;

@Tag(name = "管理后台 - 课程信息")
@RestController
@RequestMapping("/nmt/course-info")
@Validated
public class CourseInfoController {

    @Resource
    private CourseInfoService courseInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建课程信息")
    @PreAuthorize("@ss.hasPermission('nmt:course-info:create')")
    public CommonResult<Integer> createCourseInfo(@Valid @RequestBody CourseInfoSaveReqVO createReqVO) {
        return success(courseInfoService.createCourseInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程信息")
    @PreAuthorize("@ss.hasPermission('nmt:course-info:update')")
    public CommonResult<Boolean> updateCourseInfo(@Valid @RequestBody CourseInfoSaveReqVO updateReqVO) {
        courseInfoService.updateCourseInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程信息")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:course-info:delete')")
    public CommonResult<Boolean> deleteCourseInfo(@RequestParam("id") Integer id) {
        courseInfoService.deleteCourseInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程信息")
                @PreAuthorize("@ss.hasPermission('nmt:course-info:delete')")
    public CommonResult<Boolean> deleteCourseInfoList(@RequestParam("ids") List<Integer> ids) {
        courseInfoService.deleteCourseInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程信息")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:course-info:query')")
    public CommonResult<CourseInfoRespVO> getCourseInfo(@RequestParam("id") Integer id) {
        CourseInfoDO courseInfo = courseInfoService.getCourseInfo(id);
        return success(BeanUtils.toBean(courseInfo, CourseInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程信息分页")
    @PreAuthorize("@ss.hasPermission('nmt:course-info:query')")
    public CommonResult<PageResult<CourseInfoRespVO>> getCourseInfoPage(@Valid CourseInfoPageReqVO pageReqVO) {
        PageResult<CourseInfoDO> pageResult = courseInfoService.getCourseInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, CourseInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程信息 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:course-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCourseInfoExcel(@Valid CourseInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<CourseInfoDO> list = courseInfoService.getCourseInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程信息.xls", "数据", CourseInfoRespVO.class,
                        BeanUtils.toBean(list, CourseInfoRespVO.class));
    }

}