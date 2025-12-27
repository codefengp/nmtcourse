package cn.fengp.basic.module.nmt.dal.dataobject.objectivemode;

import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;
import lombok.*;

/**
 * 课程目标考核评价 扩展DO
 *
 * @author fengpeng
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ObjectiveModeExDO extends ObjectiveModeDO {

    /**
     * 评价方式
     */
    private String modeName;
    /**
     * 课程目标
     */
    private String objectiveName;

}
