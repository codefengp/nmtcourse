package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.fengp.basic.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.fengp.basic.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 达成度评价分页 Request VO")
@Data
public class AchievementEvaluationPageReqVO extends PageParam {

    @Schema(description = "课程ID", example = "28152")
    private Long courseId;

    @Schema(description = "教学班级ID", example = "10241")
    private Long classId;

    @Schema(description = "学生总体评价")
    private String overallComment;

    @Schema(description = "存在问题")
    private String problemAnalysis;

    @Schema(description = "课程改进")
    private String improvementPlan;

}