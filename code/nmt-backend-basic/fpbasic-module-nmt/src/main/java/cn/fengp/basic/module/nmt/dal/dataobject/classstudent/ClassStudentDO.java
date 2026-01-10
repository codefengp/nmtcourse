package cn.fengp.basic.module.nmt.dal.dataobject.classstudent;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 班级学生 DO
 *
 * @author fengpeng
 */
@TableName("nmt_class_student")
@KeySequence("nmt_class_student_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 学生姓名
     */
    private String name;
    /**
     * 学号
     */
    private String number;
    /**
     * 所属班级ID
     */
    private Long classId;


}