package cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 课程目标考核评价 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ObjectiveModeRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "476")
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "课程ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3596")
    @ExcelProperty("课程ID")
    private Long courseId;

    @Schema(description = "课程目标ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "3596")
    @ExcelProperty("课程目标ID")
    private Long objectiveId;

    @Schema(description = "考核方式ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9638")
    @ExcelProperty("考核方式ID")
    private Long modeId;

    @Schema(description = "分值/权重", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("分值/权重")
    private BigDecimal score;

}
