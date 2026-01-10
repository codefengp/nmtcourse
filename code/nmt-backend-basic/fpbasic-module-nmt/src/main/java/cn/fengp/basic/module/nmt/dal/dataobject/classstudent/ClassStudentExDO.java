package cn.fengp.basic.module.nmt.dal.dataobject.classstudent;

import cn.fengp.basic.framework.mybatis.core.dataobject.BaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

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
