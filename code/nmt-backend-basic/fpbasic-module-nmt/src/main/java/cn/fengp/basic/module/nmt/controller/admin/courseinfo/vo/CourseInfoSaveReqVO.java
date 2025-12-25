package cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程信息新增/修改 Request VO")
@Data
public class CourseInfoSaveReqVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24847")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "编号不能为空")
    private String number;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @Schema(description = "所属专业", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "所属专业不能为空")
    private Integer majorType;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "课程类型不能为空")
    private Integer courseType;

    @Schema(description = "课程属性", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "课程属性不能为空")
    private Integer courseProperty;

    @Schema(description = "总学时", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总学时不能为空")
    private BigDecimal courseHour;

    @Schema(description = "总学分", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "总学分不能为空")
    private BigDecimal courseScore;

    @Schema(description = "授课年级", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "授课年级不能为空")
    private Integer grade;

    @Schema(description = "授课学期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "授课学期不能为空")
    private Integer term;

    @Schema(description = "授课教师", example = "李四")
    private String teacherName;

}