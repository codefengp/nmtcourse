package cn.fengp.basic.module.nmt.service.courseobjective;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.courseobjective.CourseObjectiveMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程目标 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class CourseObjectiveServiceImpl implements CourseObjectiveService {

    @Resource
    private CourseObjectiveMapper courseObjectiveMapper;

    @Override
    public Integer createCourseObjective(CourseObjectiveSaveReqVO createReqVO) {
        // 插入
        CourseObjectiveDO courseObjective = BeanUtils.toBean(createReqVO, CourseObjectiveDO.class);
        courseObjectiveMapper.insert(courseObjective);

        // 返回
        return courseObjective.getId();
    }

    @Override
    public void updateCourseObjective(CourseObjectiveSaveReqVO updateReqVO) {
        // 校验存在
        validateCourseObjectiveExists(updateReqVO.getId());
        // 更新
        CourseObjectiveDO updateObj = BeanUtils.toBean(updateReqVO, CourseObjectiveDO.class);
        courseObjectiveMapper.updateById(updateObj);
    }

    @Override
    public void deleteCourseObjective(Integer id) {
        // 校验存在
        validateCourseObjectiveExists(id);
        // 删除
        courseObjectiveMapper.deleteById(id);
    }

    @Override
        public void deleteCourseObjectiveListByIds(List<Integer> ids) {
        // 删除
        courseObjectiveMapper.deleteByIds(ids);
        }


    private void validateCourseObjectiveExists(Integer id) {
        if (courseObjectiveMapper.selectById(id) == null) {
            throw exception(COURSE_OBJECTIVE_NOT_EXISTS);
        }
    }

    @Override
    public CourseObjectiveDO getCourseObjective(Integer id) {
        return courseObjectiveMapper.selectById(id);
    }

    @Override
    public PageResult<CourseObjectiveDO> getCourseObjectivePage(CourseObjectivePageReqVO pageReqVO) {
        return courseObjectiveMapper.selectPage(pageReqVO);
    }

}