package cn.fengp.basic.module.nmt.dal.dataobject.objectiveevaluation;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程目标达成度评价 DO
 *
 * @author fengpeng
 */
@TableName("nmt_objective_evaluation")
@KeySequence("nmt_objective_evaluation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectiveEvaluationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 课程评价主表ID
     */
    private Long evaluationId;
    /**
     * 课程目标ID
     */
    private Long objectiveId;
    /**
     * 目标名称
     */
    private String objectiveName;
    /**
     * 目标内容
     */
    private String objectiveContent;
    /**
     * 评价内容
     */
    private String comment;


}