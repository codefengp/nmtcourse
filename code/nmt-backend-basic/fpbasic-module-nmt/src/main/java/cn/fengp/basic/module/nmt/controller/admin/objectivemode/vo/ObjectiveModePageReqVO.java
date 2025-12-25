package cn.fengp.basic.module.nmt.controller.admin.objectivemode.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 课程目标考核评价分页 Request VO")
@Data
public class ObjectiveModePageReqVO extends PageParam {

    @Schema(description = "课程目标ID", example = "3596")
    private Long objectiveId;

    @Schema(description = "考核方式ID", example = "9638")
    private Long modeId;

    @Schema(description = "分值/权重")
    private BigDecimal score;

}