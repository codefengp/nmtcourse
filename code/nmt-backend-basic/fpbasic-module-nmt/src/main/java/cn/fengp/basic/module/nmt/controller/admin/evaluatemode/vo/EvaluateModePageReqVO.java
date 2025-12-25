package cn.fengp.basic.module.nmt.controller.admin.evaluatemode.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 考核评价方式分页 Request VO")
@Data
public class EvaluateModePageReqVO extends PageParam {

    @Schema(description = "评价方式")
    private String name;

    @Schema(description = "权重")
    private BigDecimal weight;

    @Schema(description = "课程ID")
    private Long courseId;

}