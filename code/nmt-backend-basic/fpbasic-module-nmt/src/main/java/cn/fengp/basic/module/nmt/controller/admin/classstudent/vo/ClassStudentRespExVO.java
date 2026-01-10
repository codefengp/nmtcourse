package cn.fengp.basic.module.nmt.controller.admin.classstudent.vo;

import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ClassStudentRespExVO extends ClassStudentRespVO{
    @Schema(description = "所属班级", requiredMode = Schema.RequiredMode.REQUIRED, example = "7044")
    @ExcelProperty("所属班级")
    private String  className;

}
