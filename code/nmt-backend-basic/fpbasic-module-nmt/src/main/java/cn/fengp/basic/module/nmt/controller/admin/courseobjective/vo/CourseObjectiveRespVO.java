package cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程目标 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseObjectiveRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "内容")
    @ExcelProperty("内容")
    private String content;

    @Schema(description = "期望值")
    @ExcelProperty("期望值")
    private BigDecimal expectValue;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("课程ID")
    private Long courseId;

}
