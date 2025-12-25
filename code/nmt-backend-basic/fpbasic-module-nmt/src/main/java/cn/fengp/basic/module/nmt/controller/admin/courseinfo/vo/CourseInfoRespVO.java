package cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.fengp.basic.framework.excel.core.annotations.DictFormat;
import cn.fengp.basic.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 课程信息 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CourseInfoRespVO {

    @Schema(description = "主键ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24847")
    @ExcelProperty("主键ID")
    private Long id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("编号")
    private String number;

    @Schema(description = "名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("名称")
    private String name;

    @Schema(description = "所属专业", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "所属专业", converter = DictConvert.class)
    @DictFormat("nmt_major_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer majorType;

    @Schema(description = "课程类型", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "课程类型", converter = DictConvert.class)
    @DictFormat("nmt_course_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer courseType;

    @Schema(description = "课程属性", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "课程属性", converter = DictConvert.class)
    @DictFormat("nmt_course_property") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer courseProperty;

    @Schema(description = "总学时", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总学时")
    private BigDecimal courseHour;

    @Schema(description = "总学分", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("总学分")
    private BigDecimal courseScore;

    @Schema(description = "授课年级", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("授课年级")
    private Integer grade;

    @Schema(description = "授课学期", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "授课学期", converter = DictConvert.class)
    @DictFormat("nmt_term") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer term;

    @Schema(description = "授课教师", example = "李四")
    @ExcelProperty("授课教师")
    private String teacherName;

    @Schema(description = "创建时间")
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}
