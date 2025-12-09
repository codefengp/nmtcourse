package cn.fengp.basic.module.nmt.service.courseinfo;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.courseinfo.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.courseinfo.CourseInfoDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.courseinfo.CourseInfoMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 课程信息 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class CourseInfoServiceImpl implements CourseInfoService {

    @Resource
    private CourseInfoMapper courseInfoMapper;

    @Override
    public Integer createCourseInfo(CourseInfoSaveReqVO createReqVO) {
        // 插入
        CourseInfoDO courseInfo = BeanUtils.toBean(createReqVO, CourseInfoDO.class);
        courseInfoMapper.insert(courseInfo);

        // 返回
        return courseInfo.getId();
    }

    @Override
    public void updateCourseInfo(CourseInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateCourseInfoExists(updateReqVO.getId());
        // 更新
        CourseInfoDO updateObj = BeanUtils.toBean(updateReqVO, CourseInfoDO.class);
        courseInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteCourseInfo(Integer id) {
        // 校验存在
        validateCourseInfoExists(id);
        // 删除
        courseInfoMapper.deleteById(id);
    }

    @Override
        public void deleteCourseInfoListByIds(List<Integer> ids) {
        // 删除
        courseInfoMapper.deleteByIds(ids);
        }


    private void validateCourseInfoExists(Integer id) {
        if (courseInfoMapper.selectById(id) == null) {
            throw exception(COURSE_INFO_NOT_EXISTS);
        }
    }

    @Override
    public CourseInfoDO getCourseInfo(Integer id) {
        return courseInfoMapper.selectById(id);
    }

    @Override
    public PageResult<CourseInfoDO> getCourseInfoPage(CourseInfoPageReqVO pageReqVO) {
        return courseInfoMapper.selectPage(pageReqVO);
    }

}