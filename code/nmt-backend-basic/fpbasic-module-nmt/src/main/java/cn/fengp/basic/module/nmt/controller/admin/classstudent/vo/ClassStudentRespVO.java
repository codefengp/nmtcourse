package cn.fengp.basic.module.nmt.controller.admin.classstudent.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 班级学生 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ClassStudentRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "21273")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "学生姓名", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("学生姓名")
    private String name;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("学号")
    private String number;

    @Schema(description = "所属班级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7044")
    @ExcelProperty("所属班级ID")
    private Long classId;

}