package cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 考核评价方式新增/修改 Request VO")
@Data
public class EvaluateModeSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long id;

    @Schema(description = "评价方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "评价方式不能为空")
    private String name;

    @Schema(description = "权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "权重不能为空")
    private BigDecimal weight;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

}