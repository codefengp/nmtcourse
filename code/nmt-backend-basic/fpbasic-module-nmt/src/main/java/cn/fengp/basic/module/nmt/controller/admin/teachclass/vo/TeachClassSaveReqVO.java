package cn.fengp.basic.module.nmt.controller.admin.teachclass.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 教学班级新增/修改 Request VO")
@Data
public class TeachClassSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3693")
    private Long id;

    @Schema(description = "班级编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "班级编号不能为空")
    private String number;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "班级名称不能为空")
    private String name;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31570")
    @NotNull(message = "课程ID不能为空")
    private Long courseId;

    @Schema(description = "班级人数")
    private Integer totalNumber;

    @Schema(description = "负责教师", example = "赵六")
    private String teacherName;

}