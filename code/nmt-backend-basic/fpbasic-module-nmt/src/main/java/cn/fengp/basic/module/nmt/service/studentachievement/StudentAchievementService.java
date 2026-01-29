package cn.fengp.basic.module.nmt.service.studentachievement;

import java.util.*;

import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementPlanDO;
import com.alibaba.fastjson.JSONObject;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.studentachievement.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.studentachievement.StudentAchievementDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 学生成绩 Service 接口
 *
 * @author fengpeng
 */
public interface StudentAchievementService {

    /**
     * 创建学生成绩
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createStudentAchievement(@Valid StudentAchievementSaveReqVO createReqVO);

    /**
     * 更新学生成绩
     *
     * @param updateReqVO 更新信息
     */
    void updateStudentAchievement(@Valid StudentAchievementSaveReqVO updateReqVO);

    /**
     * 删除学生成绩
     *
     * @param id 编号
     */
    void deleteStudentAchievement(Long id);

    /**
    * 批量删除学生成绩
    *
    * @param ids 编号
    */
    void deleteStudentAchievementListByIds(List<Long> ids);

    /**
     * 获得学生成绩
     *
     * @param id 编号
     * @return 学生成绩
     */
    StudentAchievementDO getStudentAchievement(Long id);

    /**
     * 获得学生成绩分页
     *
     * @param pageReqVO 分页查询
     * @return 学生成绩分页
     */
    PageResult<StudentAchievementDO> getStudentAchievementPage(StudentAchievementPageReqVO pageReqVO);

    /**
     * 获得学生成绩
     *
     * @param classId 班级
     * @return 学生成绩
     */
    List<StudentAchievementDO> listStudentAchievement(Long classId);

    void downloadTemplate(HttpServletResponse response, JSONObject params);
}