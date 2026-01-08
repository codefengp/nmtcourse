package cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程考核计划 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EvaluatePlanRespExVO extends EvaluatePlanRespVO{
    @Schema(description = "课程目标")
    @ExcelProperty("课程目标")
    private String objectiveName;

    @Schema(description = "考核方式")
    @ExcelProperty("考核方式")
    private String modeName;

    @Schema(description = "总分", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总分")
    private BigDecimal totalScore;

}
