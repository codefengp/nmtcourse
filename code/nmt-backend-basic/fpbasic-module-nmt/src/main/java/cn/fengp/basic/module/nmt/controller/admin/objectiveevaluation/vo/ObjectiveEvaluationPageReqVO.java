package cn.fengp.basic.module.nmt.controller.admin.objectiveevaluation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程目标达成度评价分页 Request VO")
@Data
public class ObjectiveEvaluationPageReqVO extends PageParam {

    @Schema(description = "课程评价主表ID", example = "1862")
    private Long evaluationId;

    @Schema(description = "课程目标ID", example = "27405")
    private Long objectiveId;

    @Schema(description = "目标名称")
    private String objectiveName;

    @Schema(description = "目标内容")
    private String objectiveContent;

    @Schema(description = "评价内容")
    private String comment;

}