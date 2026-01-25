package cn.fengp.basic.module.nmt.dal.dataobject.studentachievement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 学生成绩扩展 DO
 *
 * @author fengpeng
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class StudentAchievementPlanDO extends StudentAchievementDO {
    /**
     * 姓名
     */
    private String studentName;
    /**
     * 学号
     */
    private String studentNumber;


}
