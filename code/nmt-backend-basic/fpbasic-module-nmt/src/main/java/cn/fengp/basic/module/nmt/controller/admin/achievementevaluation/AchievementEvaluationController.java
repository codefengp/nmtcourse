package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation;

import com.alibaba.fastjson.JSONObject;
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

import cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.achievementevaluation.AchievementEvaluationDO;
import cn.fengp.basic.module.nmt.service.achievementevaluation.AchievementEvaluationService;

@Tag(name = "管理后台 - 达成度评价")
@RestController
@RequestMapping("/nmt/achievement-evaluation")
@Validated
public class AchievementEvaluationController {

    @Resource
    private AchievementEvaluationService achievementEvaluationService;

    @PostMapping("/create")
    @Operation(summary = "创建达成度评价")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:create')")
    public CommonResult<Long> createAchievementEvaluation(@Valid @RequestBody AchievementEvaluationSaveReqVO createReqVO) {
        return success(achievementEvaluationService.createAchievementEvaluation(createReqVO));
    }

    @PostMapping("/save")
    @Operation(summary = "保存达成度评价")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:create')")
    public CommonResult<Long> saveAchievementEvaluation(@Valid @RequestBody AchievementEvaluationSaveExReqVO createReqVO) {
        return success(achievementEvaluationService.saveAchievementEvaluation(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新达成度评价")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:update')")
    public CommonResult<Boolean> updateAchievementEvaluation(@Valid @RequestBody AchievementEvaluationSaveReqVO updateReqVO) {
        achievementEvaluationService.updateAchievementEvaluation(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除达成度评价")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:delete')")
    public CommonResult<Boolean> deleteAchievementEvaluation(@RequestParam("id") Long id) {
        achievementEvaluationService.deleteAchievementEvaluation(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除达成度评价")
                @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:delete')")
    public CommonResult<Boolean> deleteAchievementEvaluationList(@RequestParam("ids") List<Long> ids) {
        achievementEvaluationService.deleteAchievementEvaluationListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得达成度评价")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:query')")
    public CommonResult<AchievementEvaluationRespVO> getAchievementEvaluation(@RequestParam("id") Long id) {
        AchievementEvaluationDO achievementEvaluation = achievementEvaluationService.getAchievementEvaluation(id);
        return success(BeanUtils.toBean(achievementEvaluation, AchievementEvaluationRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得达成度评价分页")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:query')")
    public CommonResult<PageResult<AchievementEvaluationRespVO>> getAchievementEvaluationPage(@Valid AchievementEvaluationPageReqVO pageReqVO) {
        PageResult<AchievementEvaluationDO> pageResult = achievementEvaluationService.getAchievementEvaluationPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AchievementEvaluationRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出达成度评价 Excel")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAchievementEvaluationExcel(@Valid AchievementEvaluationPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<AchievementEvaluationDO> list = achievementEvaluationService.getAchievementEvaluationPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "达成度评价.xls", "数据", AchievementEvaluationRespVO.class,
                        BeanUtils.toBean(list, AchievementEvaluationRespVO.class));
    }

    @GetMapping("/get-overall-score")
    @Operation(summary = "获取课程总评分数")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:query')")
    public CommonResult<JSONObject> getCourseOverallScore(@RequestParam(name = "courseId") Long courseId, @RequestParam(name = "classId") Long classId) {
        JSONObject overallScore = achievementEvaluationService.getCourseOverallScore(courseId, classId);
        return success(overallScore);
    }

    @GetMapping("/get-obj-ache-eval")
    @Operation(summary = "查询课程目标达成评价结果")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:query')")
    public CommonResult<JSONObject> getObjectiveAchievementEvaluation(@RequestParam(name = "courseId") Long courseId, @RequestParam(name = "classId") Long classId) {
        JSONObject objAcheEval = achievementEvaluationService.getObjectiveAchievementEvaluation(courseId, classId);
        return success(objAcheEval);
    }

    @PostMapping("/export-report")
    @Operation(summary = "导出达成度评价报告")
    @PreAuthorize("@ss.hasPermission('nmt:achievement-evaluation:query')")
    public void exportReport(HttpServletResponse response,@RequestBody ExportReportDTO dto) throws IOException {
        achievementEvaluationService.exportReport(response,dto);
    }
}