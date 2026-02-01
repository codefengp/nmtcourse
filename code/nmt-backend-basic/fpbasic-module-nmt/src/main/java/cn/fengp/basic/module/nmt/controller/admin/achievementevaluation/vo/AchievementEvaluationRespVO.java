package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 达成度评价 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AchievementEvaluationRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "5462")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "28152")
    @ExcelProperty("课程ID")
    private Long courseId;

    @Schema(description = "教学班级ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10241")
    @ExcelProperty("教学班级ID")
    private Long classId;

    @Schema(description = "学生总体评价")
    @ExcelProperty("学生总体评价")
    private String overallComment;

    @Schema(description = "存在问题")
    @ExcelProperty("存在问题")
    private String problemAnalysis;

    @Schema(description = "课程改进")
    @ExcelProperty("课程改进")
    private String improvementPlan;

}