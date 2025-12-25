package cn.fengp.basic.module.nmt.dal.dataobject.evaluatemode;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 考核评价方式 DO
 *
 * @author fengpeng
 */
@TableName("nmt_evaluate_mode")
@KeySequence("nmt_evaluate_mode_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EvaluateModeDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 评价方式
     */
    private String name;
    /**
     * 权重
     */
    private BigDecimal weight;
    /**
     * 课程ID
     */
    private Long courseId;


}