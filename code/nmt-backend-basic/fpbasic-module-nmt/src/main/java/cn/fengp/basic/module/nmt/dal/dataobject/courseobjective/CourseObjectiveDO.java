package cn.fengp.basic.module.nmt.dal.dataobject.courseobjective;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程目标 DO
 *
 * @author fengpeng
 */
@TableName("nmt_course_objective")
@KeySequence("nmt_course_objective_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseObjectiveDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Integer id;
    /**
     * 名称
     */
    private String name;
    /**
     * 内容
     */
    private String content;
    /**
     * 期望值
     */
    private BigDecimal expectValue;
    /**
     * 课程ID
     */
    private Integer courseId;


}