package cn.fengp.basic.module.nmt.controller.admin.evaluatemode;

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

import cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode.EvaluateModeDO;
import cn.fengp.basic.module.nmt.service.evaluatemode.EvaluateModeService;

@Tag(name = "管理后台 - 考核评价方式")
@RestController
@RequestMapping("/nmt/evaluate-mode")
@Validated
public class EvaluateModeController {

    @Resource
    private EvaluateModeService evaluateModeService;

    @PostMapping("/create")
    @Operation(summary = "创建考核评价方式")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:create')")
    public CommonResult<Long> createEvaluateMode(@Valid @RequestBody EvaluateModeSaveReqVO createReqVO) {
        return success(evaluateModeService.createEvaluateMode(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新考核评价方式")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:update')")
    public CommonResult<Boolean> updateEvaluateMode(@Valid @RequestBody EvaluateModeSaveReqVO updateReqVO) {
        evaluateModeService.updateEvaluateMode(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除考核评价方式")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:delete')")
    public CommonResult<Boolean> deleteEvaluateMode(@RequestParam("id") Long id) {
        evaluateModeService.deleteEvaluateMode(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除考核评价方式")
                @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:delete')")
    public CommonResult<Boolean> deleteEvaluateModeList(@RequestParam("ids") List<Long> ids) {
        evaluateModeService.deleteEvaluateModeListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得考核评价方式")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:query')")
    public CommonResult<EvaluateModeRespVO> getEvaluateMode(@RequestParam("id") Long id) {
        EvaluateModeDO evaluateMode = evaluateModeService.getEvaluateMode(id);
        return success(BeanUtils.toBean(evaluateMode, EvaluateModeRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得考核评价方式分页")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:query')")
    public CommonResult<PageResult<EvaluateModeRespVO>> getEvaluateModePage(@Valid EvaluateModePageReqVO pageReqVO) {
        PageResult<EvaluateModeDO> pageResult = evaluateModeService.getEvaluateModePage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EvaluateModeRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出考核评价方式 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-mode:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEvaluateModeExcel(@Valid EvaluateModePageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EvaluateModeDO> list = evaluateModeService.getEvaluateModePage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "考核评价方式.xls", "数据", EvaluateModeRespVO.class,
                        BeanUtils.toBean(list, EvaluateModeRespVO.class));
    }

}