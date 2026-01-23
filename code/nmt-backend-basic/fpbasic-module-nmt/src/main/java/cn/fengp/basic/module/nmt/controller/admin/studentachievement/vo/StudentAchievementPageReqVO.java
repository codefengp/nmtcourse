package cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import java.math.BigDecimal;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 学生成绩分页 Request VO")
@Data
public class StudentAchievementPageReqVO extends PageParam {

    @Schema(description = "学生ID", example = "30048")
    private Long studentId;

    @Schema(description = "考核计划ID", example = "124")
    private Long planId;

    @Schema(description = "课程目标ID", example = "1894")
    private Long objectiveId;

    @Schema(description = "考核方式ID", example = "12748")
    private Long modeId;

    @Schema(description = "得分")
    private BigDecimal score;

}