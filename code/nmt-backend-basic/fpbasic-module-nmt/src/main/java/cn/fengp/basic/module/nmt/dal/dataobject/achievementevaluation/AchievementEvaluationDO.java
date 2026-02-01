package cn.fengp.basic.module.nmt.dal.dataobject.achievementevaluation;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 达成度评价 DO
 *
 * @author fengpeng
 */
@TableName("nmt_achievement_evaluation")
@KeySequence("nmt_achievement_evaluation_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AchievementEvaluationDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 课程ID
     */
    private Long courseId;
    /**
     * 教学班级ID
     */
    private Long classId;
    /**
     * 学生总体评价
     */
    private String overallComment;
    /**
     * 存在问题
     */
    private String problemAnalysis;
    /**
     * 课程改进
     */
    private String improvementPlan;


}