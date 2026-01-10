package cn.fengp.basic.module.nmt.controller.admin.classstudent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import jakarta.validation.constraints.*;

@Schema(description = "管理后台 - 班级学生新增/修改 Request VO")
@Data
public class ClassStudentSaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21273")
    private Long id;

    @Schema(description = "学生姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "学生姓名不能为空")
    private String name;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "学号不能为空")
    private String number;

    @Schema(description = "所属班级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7044")
    @NotNull(message = "所属班级ID不能为空")
    private Long classId;

}