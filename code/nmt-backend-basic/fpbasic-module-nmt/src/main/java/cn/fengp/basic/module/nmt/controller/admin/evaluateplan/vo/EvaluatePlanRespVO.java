package cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程考核计划 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EvaluatePlanRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31826")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "目标评价ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4620")
    @ExcelProperty("目标评价ID")
    private Long objectiveModeId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5695")
    @ExcelProperty("课程目标ID")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18080")
    @ExcelProperty("考核方式ID")
    private Long modeId;

    @Schema(description = "分数/权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("分数/权重")
    private BigDecimal score;

    @Schema(description = "考核内容")
    @ExcelProperty("考核内容")
    private String content;

}