package cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 课程目标达成度评价新增/修改 Request VO")
@Data
public class ObjectiveEvaluationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "16067")
    private Long id;

    @Schema(description = "课程评价主表ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1862")
    @NotNull(message = "课程评价主表ID不能为空")
    private Long evaluationId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "27405")
    @NotNull(message = "课程目标ID不能为空")
    private Long objectiveId;

    @Schema(description = "目标名称")
    private String objectiveName;

    @Schema(description = "目标内容")
    private String objectiveContent;

    @Schema(description = "评价内容")
    private String comment;

}