package cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程目标新增/修改 Request VO")
@Data
public class CourseObjectiveSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "期望值")
    private BigDecimal expectValue;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;

}