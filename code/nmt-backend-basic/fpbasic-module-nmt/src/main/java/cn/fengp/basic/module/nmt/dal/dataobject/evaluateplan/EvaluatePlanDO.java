package cn.fengp.basic.module.nmt.dal.dataobject.evaluateplan;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程考核计划 DO
 *
 * @author fengpeng
 */
@TableName("nmt_evaluate_plan")
@KeySequence("nmt_evaluate_plan_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluatePlanDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 目标评价ID
     */
    private Long objectiveModeId;
    /**
     * 课程目标ID
     */
    private Long objectiveId;
    /**
     * 考核方式ID
     */
    private Long modeId;
    /**
     * 分数/权重
     */
    private BigDecimal score;
    /**
     * 考核内容
     */
    private String content;


}