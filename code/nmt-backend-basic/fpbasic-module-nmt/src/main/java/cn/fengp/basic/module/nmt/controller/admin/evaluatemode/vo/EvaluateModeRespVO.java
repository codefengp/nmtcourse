package cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 考核评价方式 Response VO")
@Data
@ExcelIgnoreUnannotated
public class EvaluateModeRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "评价方式", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("评价方式")
    private String name;

    @Schema(description = "权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("权重")
    private BigDecimal weight;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("课程ID")
    private Long courseId;

}