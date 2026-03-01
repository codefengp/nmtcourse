package cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation;

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

import cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation.ObjectiveEvaluationDO;
import cn.fengp.basic.module.nmt.service.objectiveevaluation.ObjectiveEvaluationService;

@Tag(name = "管理后台 - 课程目标达成度评价")
@RestController
@RequestMapping("/nmt/objective-evaluation")
@Validated
public class ObjectiveEvaluationController {

    @Resource
    private ObjectiveEvaluationService objectiveEvaluationService;

    @PostMapping("/create")
    @Operation(summary = "创建课程目标达成度评价")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:create')")
    public CommonResult<Long> createObjectiveEvaluation(@Valid @RequestBody ObjectiveEvaluationSaveReqVO createReqVO) {
        return success(objectiveEvaluationService.createObjectiveEvaluation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新课程目标达成度评价")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:update')")
    public CommonResult<Boolean> updateObjectiveEvaluation(@Valid @RequestBody ObjectiveEvaluationSaveReqVO updateReqVO) {
        objectiveEvaluationService.updateObjectiveEvaluation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程目标达成度评价")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:delete')")
    public CommonResult<Boolean> deleteObjectiveEvaluation(@RequestParam("id") Long id) {
        objectiveEvaluationService.deleteObjectiveEvaluation(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程目标达成度评价")
                @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:delete')")
    public CommonResult<Boolean> deleteObjectiveEvaluationList(@RequestParam("ids") List<Long> ids) {
        objectiveEvaluationService.deleteObjectiveEvaluationListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程目标达成度评价")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:query')")
    public CommonResult<ObjectiveEvaluationRespVO> getObjectiveEvaluation(@RequestParam("id") Long id) {
        ObjectiveEvaluationDO objectiveEvaluation = objectiveEvaluationService.getObjectiveEvaluation(id);
        return success(BeanUtils.toBean(objectiveEvaluation, ObjectiveEvaluationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程目标达成度评价分页")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:query')")
    public CommonResult<PageResult<ObjectiveEvaluationRespVO>> getObjectiveEvaluationPage(@Valid ObjectiveEvaluationPageReqVO pageReqVO) {
        PageResult<ObjectiveEvaluationDO> pageResult = objectiveEvaluationService.getObjectiveEvaluationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, ObjectiveEvaluationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程目标达成度评价 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportObjectiveEvaluationExcel(@Valid ObjectiveEvaluationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<ObjectiveEvaluationDO> list = objectiveEvaluationService.getObjectiveEvaluationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程目标达成度评价.xls", "数据", ObjectiveEvaluationRespVO.class,
                        BeanUtils.toBean(list, ObjectiveEvaluationRespVO.class));
    }

    @GetMapping("/get-by-achi")
    @Operation(summary = "获得课程目标达成度评价集合")
    @Parameter(name = "id", description = "达成度编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:objective-evaluation:query')")
    public CommonResult<List<ObjectiveEvaluationRespVO>> getByAchievementEvaluation(@RequestParam("id") Long id) {
        List<ObjectiveEvaluationDO> byAchievementEvaluations = objectiveEvaluationService.getByAchievementEvaluation(id);
        return success(BeanUtils.toBean(byAchievementEvaluations, ObjectiveEvaluationRespVO.class));
    }

}