package cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 学生成绩新增/修改 Request VO")
@Data
public class StudentAchievementSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4889")
    private Long id;

    @Schema(description = "学生ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30048")
    @NotNull(message = "学生ID不能为空")
    private Long studentId;

    @Schema(description = "考核计划ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "124")
    @NotNull(message = "考核计划ID不能为空")
    private Long planId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1894")
    @NotNull(message = "课程目标ID不能为空")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12748")
    @NotNull(message = "考核方式ID不能为空")
    private Long modeId;

    @Schema(description = "得分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "得分不能为空")
    private BigDecimal score;

}