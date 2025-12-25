package cn.fengp.basic.module.nmt.dal.dataobject.objectivemode;

import lombok.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;

/**
 * 课程目标考核评价 DO
 *
 * @author fengpeng
 */
@TableName("nmt_objective_mode")
@KeySequence("nmt_objective_mode_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ObjectiveModeDO extends BaseDO {

    /**
     * 主键ID
     */
    @TableId
    private Long id;
    /**
     * 课程目标ID
     */
    private Long objectiveId;
    /**
     * 考核方式ID
     */
    private Long modeId;
    /**
     * 分值/权重
     */
    private BigDecimal score;


}