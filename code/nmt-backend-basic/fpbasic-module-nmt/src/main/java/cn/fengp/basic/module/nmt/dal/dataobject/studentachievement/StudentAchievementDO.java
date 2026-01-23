package cn.fengp.basic.module.nmt.dal.dataobject.studentachievement;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 学生成绩 DO
 *
 * @author fengpeng
 */
@TableName("nmt_student_achievement")
@KeySequence("nmt_student_achievement_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAchievementDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 学生ID
     */
    private Long studentId;
    /**
     * 考核计划ID
     */
    private Long planId;
    /**
     * 课程目标ID
     */
    private Long objectiveId;
    /**
     * 考核方式ID
     */
    private Long modeId;
    /**
     * 得分
     */
    private BigDecimal score;


}