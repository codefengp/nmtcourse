package cn.fengp.basic.module.nmt.service.courseobjective;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.CourseObjectivePageReqVO;
import cn.fengp.basic.module.nmt.controller.admin.courseobjective.vo.CourseObjectiveSaveReqVO;
import cn.fengp.basic.module.nmt.dal.dataobject.courseobjective.CourseObjectiveDO;
import cn.fengp.basic.module.nmt.dal.mysql.courseobjective.CourseObjectiveMapper;
import cn.fengp.basic.module.nmt.service.objectivemode.ObjectiveModeService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.COURSE_OBJECTIVE_NOT_EXISTS;

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

    @Resource
    private ObjectiveModeService objectiveModeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createCourseObjective(CourseObjectiveSaveReqVO createReqVO) {
        // 插入
        CourseObjectiveDO courseObjective = BeanUtils.toBean(createReqVO, CourseObjectiveDO.class);
        courseObjectiveMapper.insert(courseObjective);
        //新增矩阵数据
        objectiveModeService.checkWithAddEvaluateMode(courseObjective.getCourseId(),courseObjective.getId(),true);
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
    public void deleteCourseObjective(Long id) {
        // 校验存在
        validateCourseObjectiveExists(id);
        // 删除
        courseObjectiveMapper.deleteById(id);
        //删除矩阵数据
        objectiveModeService.checkWithRemoveEvaluateMode(id,true);
    }

    @Override
        public void deleteCourseObjectiveListByIds(List<Long> ids) {
        // 删除
        courseObjectiveMapper.deleteByIds(ids);
        }


    private void validateCourseObjectiveExists(Long id) {
        if (courseObjectiveMapper.selectById(id) == null) {
            throw exception(COURSE_OBJECTIVE_NOT_EXISTS);
        }
    }

    @Override
    public CourseObjectiveDO getCourseObjective(Long id) {
        return courseObjectiveMapper.selectById(id);
    }

    @Override
    public PageResult<CourseObjectiveDO> getCourseObjectivePage(CourseObjectivePageReqVO pageReqVO) {
        return courseObjectiveMapper.selectPage(pageReqVO);
    }

    @Override
    public List<CourseObjectiveDO> getCourseObjectiveList() {
        return courseObjectiveMapper.selectList();
    }

}