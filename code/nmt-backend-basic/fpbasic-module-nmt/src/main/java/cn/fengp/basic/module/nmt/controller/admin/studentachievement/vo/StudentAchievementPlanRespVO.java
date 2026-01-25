package cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 学生成绩扩展 Response VO")
@Data
@ExcelIgnoreUnannotated
public class StudentAchievementPlanRespVO extends StudentAchievementRespVO{
    @Schema(description = "姓名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("姓名")
    private String studentName;

    @Schema(description = "学号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("学号")
    private String studentNumber;
}
