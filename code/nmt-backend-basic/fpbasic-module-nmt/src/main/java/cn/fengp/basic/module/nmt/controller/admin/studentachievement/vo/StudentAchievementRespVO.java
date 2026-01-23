package cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 学生成绩 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StudentAchievementRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "4889")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "学生ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "30048")
    @ExcelProperty("学生ID")
    private Long studentId;

    @Schema(description = "考核计划ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "124")
    @ExcelProperty("考核计划ID")
    private Long planId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1894")
    @ExcelProperty("课程目标ID")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "12748")
    @ExcelProperty("考核方式ID")
    private Long modeId;

    @Schema(description = "得分", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("得分")
    private BigDecimal score;

}