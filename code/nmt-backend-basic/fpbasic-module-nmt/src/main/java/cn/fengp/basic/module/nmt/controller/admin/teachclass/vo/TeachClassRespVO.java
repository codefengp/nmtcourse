package cn.fengp.basic.module.nmt.controller.admin.teachclass.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 教学班级 Response VO")
@Data
@ExcelIgnoreUnannotated
public class TeachClassRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "3693")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "班级编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("班级编号")
    private String number;

    @Schema(description = "班级名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("班级名称")
    private String name;

    @Schema(description = "班级人数")
    @ExcelProperty("班级人数")
    private Integer totalNumber;

    @Schema(description = "负责教师", example = "赵六")
    @ExcelProperty("负责教师")
    private String teacherName;

}