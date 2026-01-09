package cn.fengp.basic.module.nmt.dal.dataobject.teachclass;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 教学班级 DO
 *
 * @author fengpeng
 */
@TableName("nmt_teach_class")
@KeySequence("nmt_teach_class_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeachClassDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 班级编号
     */
    private String number;
    /**
     * 班级名称
     */
    private String name;
    /**
     * 课程ID
     */
    private Long courseId;
    /**
     * 班级人数
     */
    private Integer totalNumber;
    /**
     * 负责教师
     */
    private String teacherName;


}