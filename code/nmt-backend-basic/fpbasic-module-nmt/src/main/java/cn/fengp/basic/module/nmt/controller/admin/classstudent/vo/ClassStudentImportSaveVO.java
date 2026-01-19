package cn.fengp.basic.module.nmt.controller.admin.classstudent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Schema(description = "管理后台 - 班级学生新增/修改 Request VO")
@Data
public class ClassStudentImportSaveVO {

    @Schema(description = "学生姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "学生姓名不能为空")
    private String name;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "学号不能为空")
    private String number;

}