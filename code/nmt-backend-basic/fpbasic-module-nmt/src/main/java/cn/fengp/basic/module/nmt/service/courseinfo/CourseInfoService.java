package cn.fengp.basic.module.nmt.service.courseinfo;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseinfo.CourseInfoDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 课程信息 Service 接口
 *
 * @author fengpeng
 */
public interface CourseInfoService {

    /**
     * 创建课程信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Integer createCourseInfo(@Valid CourseInfoSaveReqVO createReqVO);

    /**
     * 更新课程信息
     *
     * @param updateReqVO 更新信息
     */
    void updateCourseInfo(@Valid CourseInfoSaveReqVO updateReqVO);

    /**
     * 删除课程信息
     *
     * @param id 编号
     */
    void deleteCourseInfo(Integer id);

    /**
    * 批量删除课程信息
    *
    * @param ids 编号
    */
    void deleteCourseInfoListByIds(List<Integer> ids);

    /**
     * 获得课程信息
     *
     * @param id 编号
     * @return 课程信息
     */
    CourseInfoDO getCourseInfo(Integer id);

    /**
     * 获得课程信息分页
     *
     * @param pageReqVO 分页查询
     * @return 课程信息分页
     */
    PageResult<CourseInfoDO> getCourseInfoPage(CourseInfoPageReqVO pageReqVO);

}