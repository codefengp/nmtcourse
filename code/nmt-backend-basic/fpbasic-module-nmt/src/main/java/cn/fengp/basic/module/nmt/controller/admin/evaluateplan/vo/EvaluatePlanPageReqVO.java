package cn.fengp.basic.module.nmt.controller.admin.evaluateplan.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程考核计划分页 Request VO")
@Data
public class EvaluatePlanPageReqVO extends PageParam {

    @Schema(description = "目标评价ID", example = "4620")
    private Long objectiveModeId;

    @Schema(description = "课程目标ID", example = "5695")
    private Long objectiveId;

    @Schema(description = "考核方式ID", example = "18080")
    private Long modeId;

    @Schema(description = "分数/权重")
    private BigDecimal score;

    @Schema(description = "考核内容")
    private String content;

}