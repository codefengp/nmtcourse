package cn.fengp.basic.module.nmt.service.courseobjective;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 课程目标 Service 接口
 *
 * @author fengpeng
 */
public interface CourseObjectiveService {

    /**
     * 创建课程目标
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createCourseObjective(@Valid CourseObjectiveSaveReqVO createReqVO);

    /**
     * 更新课程目标
     *
     * @param updateReqVO 更新信息
     */
    void updateCourseObjective(@Valid CourseObjectiveSaveReqVO updateReqVO);

    /**
     * 删除课程目标
     *
     * @param id 编号
     */
    void deleteCourseObjective(Integer id);

    /**
    * 批量删除课程目标
    *
    * @param ids 编号
    */
    void deleteCourseObjectiveListByIds(List<Integer> ids);

    /**
     * 获得课程目标
     *
     * @param id 编号
     * @return 课程目标
     */
    CourseObjectiveDO getCourseObjective(Integer id);

    /**
     * 获得课程目标分页
     *
     * @param pageReqVO 分页查询
     * @return 课程目标分页
     */
    PageResult<CourseObjectiveDO> getCourseObjectivePage(CourseObjectivePageReqVO pageReqVO);

}