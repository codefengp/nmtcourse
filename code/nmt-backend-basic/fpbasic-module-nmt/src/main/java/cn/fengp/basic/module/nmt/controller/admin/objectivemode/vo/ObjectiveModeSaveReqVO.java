package cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程目标考核评价新增/修改 Request VO")
@Data
public class ObjectiveModeSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "476")
    private Long id;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3596")
    @NotNull(message = "课程目标ID不能为空")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9638")
    @NotNull(message = "考核方式ID不能为空")
    private Long modeId;

    @Schema(description = "分值/权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "分值/权重不能为空")
    private BigDecimal score;

}