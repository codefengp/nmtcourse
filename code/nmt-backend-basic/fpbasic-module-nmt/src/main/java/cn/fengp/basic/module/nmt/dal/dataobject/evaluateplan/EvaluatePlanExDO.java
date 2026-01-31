package cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 课程考核计划 DO 扩展
 *
 * @author fengpeng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatePlanExDO extends EvaluatePlanDO {

    /**
     * 课程目标
     */
    private String objectiveName;
    /**
     * 考核方式
     */
    private String modeName;
    /**
     * 总分
     */
    private BigDecimal totalScore;

}
