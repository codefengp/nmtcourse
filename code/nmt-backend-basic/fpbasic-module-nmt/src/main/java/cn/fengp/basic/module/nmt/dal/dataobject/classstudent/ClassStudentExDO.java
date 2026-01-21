package cn.fengp.basic.module.nmt.dal.dataobject.classstudent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 班级学生 DO
 *
 * @author fengpeng
 */
@Data
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class ClassStudentExDO extends ClassStudentDO {

    /**
     * 所属班级
     */
    private String className;

}
