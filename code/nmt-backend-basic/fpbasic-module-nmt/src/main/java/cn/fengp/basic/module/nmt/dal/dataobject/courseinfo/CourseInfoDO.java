package cn.fengp.basic.module.nmt.dal.dataobject.courseinfo;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程信息 DO
 *
 * @author fengpeng
 */
@TableName("nmt_course_info")
@KeySequence("nmt_course_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseInfoDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Integer id;
    /**
     * 编号
     */
    private String number;
    /**
     * 名称
     */
    private String name;
    /**
     * 所属专业
     *
     * 枚举 {@link TODO nmt_major_type 对应的类}
     */
    private Integer majorType;
    /**
     * 课程类型
     *
     * 枚举 {@link TODO nmt_course_type 对应的类}
     */
    private Integer courseType;
    /**
     * 课程属性
     *
     * 枚举 {@link TODO nmt_course_property 对应的类}
     */
    private Integer courseProperty;
    /**
     * 总学时
     */
    private BigDecimal courseHour;
    /**
     * 总学分
     */
    private BigDecimal courseScore;
    /**
     * 授课年级
     */
    private Integer grade;
    /**
     * 授课学期
     *
     * 枚举 {@link TODO nmt_term 对应的类}
     */
    private Integer term;
    /**
     * 授课教师
     */
    private String teacherName;


}
