package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 达成度评价新增/修改 Request VO")
@Data
public class AchievementEvaluationSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5462")
    private Long id;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28152")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Schema(description = "教学班级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10241")
    @NotNull(message = "教学班级ID不能为空")
    private Long classId;

    @Schema(description = "学生总体评价")
    private String overallComment;

    @Schema(description = "存在问题")
    private String problemAnalysis;

    @Schema(description = "课程改进")
    private String improvementPlan;

}