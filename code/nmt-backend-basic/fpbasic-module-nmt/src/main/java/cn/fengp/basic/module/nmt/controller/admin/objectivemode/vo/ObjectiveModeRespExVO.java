package cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(description = "管理后台 - 课程目标考核评价扩展 vo")
@Data
@ExcelIgnoreUnannotated
public class ObjectiveModeRespExVO extends ObjectiveModeRespVO{

    @Schema(description = "课程目标", example = "")
    private String objectiveName;

    @Schema(description = "考核方式", example = "")
    private String modeName;

}
