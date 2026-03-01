package cn.fengp.basic.module.nmt.controller.admin.achievementevaluation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AchievementEvaluationSaveExReqVO extends AchievementEvaluationSaveReqVO{
    @Schema(description = "课程目标分析")
    private String objectiveEvaluations;
}