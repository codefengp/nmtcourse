package cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程目标达成度评价 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ObjectiveEvaluationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16067")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "课程评价主表ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1862")
    @ExcelProperty("课程评价主表ID")
    private Long evaluationId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27405")
    @ExcelProperty("课程目标ID")
    private Long objectiveId;

    @Schema(description = "目标名称")
    @ExcelProperty("目标名称")
    private String objectiveName;

    @Schema(description = "目标内容")
    @ExcelProperty("目标内容")
    private String objectiveContent;

    @Schema(description = "评价内容")
    @ExcelProperty("评价内容")
    private String comment;

}