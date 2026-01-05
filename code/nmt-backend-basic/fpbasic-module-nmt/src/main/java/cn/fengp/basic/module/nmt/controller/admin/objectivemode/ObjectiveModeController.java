package cn.fengp.basic.module.nmt.controller.admin.objectivemode;

import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeExDO;
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

import cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectivemode.ObjectiveModeDO;
import cn.fengp.basic.module.nmt.service.objectivemode.ObjectiveModeService;

@Tag(name = "管理后台 - 课程目标考核评价")
@RestController
@RequestMapping("/nmt/objective-mode")
@Validated
public class ObjectiveModeController {

    @Resource
    private ObjectiveModeService objectiveModeService;

    @PostMapping("/create")
    @Operation(summary = "创建课程目标考核评价")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:create')")
    public CommonResult<Long> createObjectiveMode(@Valid @RequestBody ObjectiveModeSaveReqVO createReqVO) {
        return success(objectiveModeService.createObjectiveMode(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程目标考核评价")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:update')")
    public CommonResult<Boolean> updateObjectiveMode(@Valid @RequestBody ObjectiveModeSaveReqVO updateReqVO) {
        objectiveModeService.updateObjectiveMode(updateReqVO);
        return success(true);
    }

    @PutMapping("/update-list")
    @Operation(summary = "更新课程目标考核评价集合")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:update')")
    public CommonResult<Boolean> updateObjectiveModeList(@Valid @RequestBody List<ObjectiveModeSaveReqVO> updateReqVOs) {
        objectiveModeService.updateObjectiveModeList(updateReqVOs);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程目标考核评价")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:delete')")
    public CommonResult<Boolean> deleteObjectiveMode(@RequestParam("id") Long id) {
        objectiveModeService.deleteObjectiveMode(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程目标考核评价")
                @PreAuthorize("@ss.hasPermission('nmt:objective-mode:delete')")
    public CommonResult<Boolean> deleteObjectiveModeList(@RequestParam("ids") List<Long> ids) {
        objectiveModeService.deleteObjectiveModeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程目标考核评价")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:query')")
    public CommonResult<ObjectiveModeRespVO> getObjectiveMode(@RequestParam("id") Long id) {
        ObjectiveModeDO objectiveMode = objectiveModeService.getObjectiveMode(id);
        return success(BeanUtils.toBean(objectiveMode, ObjectiveModeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程目标考核评价分页")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:query')")
    public CommonResult<PageResult<ObjectiveModeRespVO>> getObjectiveModePage(@Valid ObjectiveModePageReqVO pageReqVO) {
        PageResult<ObjectiveModeDO> pageResult = objectiveModeService.getObjectiveModePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ObjectiveModeRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程目标考核评价列表")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:query')")
    public CommonResult<List<ObjectiveModeRespExVO>> listObjectiveMode(@RequestParam("courseId") Long courseId) {
        List<ObjectiveModeExDO> objectiveModeExDOS = objectiveModeService.listObjectiveMode(courseId);
        return success(BeanUtils.toBean(objectiveModeExDOS, ObjectiveModeRespExVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程目标考核评价 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:objective-mode:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportObjectiveModeExcel(@Valid ObjectiveModePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ObjectiveModeDO> list = objectiveModeService.getObjectiveModePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程目标考核评价.xls", "数据", ObjectiveModeRespVO.class,
                        BeanUtils.toBean(list, ObjectiveModeRespVO.class));
    }

}