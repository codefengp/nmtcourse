package cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程目标分页 Request VO")
@Data
public class CourseObjectivePageReqVO extends PageParam {

    @Schema(description = "名称")
    private String name;

    @Schema(description = "内容")
    private String content;

    @Schema(description = "期望值")
    private BigDecimal expectValue;

    @Schema(description = "课程ID")
    private Integer courseId;

}