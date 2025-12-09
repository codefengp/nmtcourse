package cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程信息分页 Request VO")
@Data
public class CourseInfoPageReqVO extends PageParam {

    @Schema(description = "编号")
    private String number;

    @Schema(description = "名称", example = "芋艿")
    private String name;

    @Schema(description = "所属专业", example = "2")
    private Integer majorType;

    @Schema(description = "课程类型", example = "1")
    private Integer courseType;

    @Schema(description = "课程属性", example = "1")
    private Integer courseProperty;

    @Schema(description = "授课年级")
    private Integer grade;

    @Schema(description = "授课学期")
    private Integer term;

    @Schema(description = "授课教师", example = "李四")
    private String teacherName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}