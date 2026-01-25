package cn.fengp.basic.module.nmt.controller.admin.evaluateplan;

import cn.fengp.basic.framework.apilog.core.annotation.ApiAccessLog;
import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.framework.excel.core.util.ExcelUtils;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.EvaluatePlanPageReqVO;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.EvaluatePlanRespExVO;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.EvaluatePlanRespVO;
import cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo.EvaluatePlanSaveReqVO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanDO;
import cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan.EvaluatePlanExDO;
import cn.fengp.basic.module.nmt.service.evaluateplan.EvaluatePlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

import static cn.fengp.basic.framework.apilog.core.enums.OperateTypeEnum.EXPORT;
import static cn.fengp.basic.framework.common.pojo.CommonResult.success;

@Tag(name = "管理后台 - 课程考核计划")
@RestController
@RequestMapping("/nmt/evaluate-plan")
@Validated
public class EvaluatePlanController {

    @Resource
    private EvaluatePlanService evaluatePlanService;

    @PostMapping("/create")
    @Operation(summary = "创建课程考核计划")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:create')")
    public CommonResult<Long> createEvaluatePlan(@Valid @RequestBody EvaluatePlanSaveReqVO createReqVO) {
        return success(evaluatePlanService.createEvaluatePlan(createReqVO));
    }
 
    @PutMapping("/update")
    @Operation(summary = "更新课程考核计划")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:update')")
    public CommonResult<Boolean> updateEvaluatePlan(@Valid @RequestBody EvaluatePlanSaveReqVO updateReqVO) {
        evaluatePlanService.updateEvaluatePlan(updateReqVO);
        return success(true);
    }

    @PutMapping("/save-list")
    @Operation(summary = "保存课程考核计划集合")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:update')")
    public CommonResult<Boolean> saveEvaluatePlanList(@Valid @RequestBody List<EvaluatePlanSaveReqVO> reqVOs) {
        evaluatePlanService.saveEvaluatePlanList(reqVOs);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除课程考核计划")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:delete')")
    public CommonResult<Boolean> deleteEvaluatePlan(@RequestParam("id") Long id) {
        evaluatePlanService.deleteEvaluatePlan(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除课程考核计划")
                @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:delete')")
    public CommonResult<Boolean> deleteEvaluatePlanList(@RequestParam("ids") List<Long> ids) {
        evaluatePlanService.deleteEvaluatePlanListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得课程考核计划")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:query')")
    public CommonResult<EvaluatePlanRespVO> getEvaluatePlan(@RequestParam("id") Long id) {
        EvaluatePlanDO evaluatePlan = evaluatePlanService.getEvaluatePlan(id);
        return success(BeanUtils.toBean(evaluatePlan, EvaluatePlanRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得课程考核计划分页")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:query')")
    public CommonResult<PageResult<EvaluatePlanRespVO>> getEvaluatePlanPage(@Valid EvaluatePlanPageReqVO pageReqVO) {
        PageResult<EvaluatePlanDO> pageResult = evaluatePlanService.getEvaluatePlanPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, EvaluatePlanRespVO.class));
    }

    @GetMapping("/list")
    @Operation(summary = "获得课程考核计划列表")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:query')")
    public CommonResult<List<EvaluatePlanRespExVO>> listEvaluatePlan(@RequestParam("courseId") Long courseId) {
        List<EvaluatePlanExDO> evaluatePlanExDOs = evaluatePlanService.listEvaluatePlan(courseId);
        return success(BeanUtils.toBean(evaluatePlanExDOs, EvaluatePlanRespExVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出课程考核计划 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:evaluate-plan:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportEvaluatePlanExcel(@Valid EvaluatePlanPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<EvaluatePlanDO> list = evaluatePlanService.getEvaluatePlanPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "课程考核计划.xls", "数据", EvaluatePlanRespVO.class,
                        BeanUtils.toBean(list, EvaluatePlanRespVO.class));
    }

}