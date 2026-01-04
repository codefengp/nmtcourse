package cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程考核计划新增/修改 Request VO")
@Data
public class EvaluatePlanSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31826")
    private Long id;

    @Schema(description = "目标评价ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4620")
    @NotNull(message = "目标评价ID不能为空")
    private Long objectiveModeId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5695")
    @NotNull(message = "课程目标ID不能为空")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "18080")
    @NotNull(message = "考核方式ID不能为空")
    private Long modeId;

    @Schema(description = "分数/权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分数/权重不能为空")
    private BigDecimal score;

    @Schema(description = "考核内容")
    private String content;

}