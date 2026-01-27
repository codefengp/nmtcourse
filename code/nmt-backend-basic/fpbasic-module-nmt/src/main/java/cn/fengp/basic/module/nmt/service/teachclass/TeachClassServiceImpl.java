package cn.fengp.basic.module.nmt.service.teachclass;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import jakarta.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.fengp.basic.module.nmt.controller.admin.teachclass.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.teachclass.TeachClassDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;
import cn.fengp.basic.framework.common.util.object.BeanUtils;

import cn.fengp.basic.module.nmt.dal.mysql.teachclass.TeachClassMapper;

import static cn.fengp.basic.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.convertList;
import static cn.fengp.basic.framework.common.util.collection.CollectionUtils.diffList;
import static cn.fengp.basic.module.nmt.enums.ErrorCodeConstants.*;

/**
 * 教学班级 Service 实现类
 *
 * @author fengpeng
 */
@Service
@Validated
public class TeachClassServiceImpl implements TeachClassService {

    @Resource
    private TeachClassMapper teachClassMapper;

    @Override
    public Long createTeachClass(TeachClassSaveReqVO createReqVO) {
        // 插入
        TeachClassDO teachClass = BeanUtils.toBean(createReqVO, TeachClassDO.class);
        teachClassMapper.insert(teachClass);

        // 返回
        return teachClass.getId();
    }

    @Override
    public void updateTeachClass(TeachClassSaveReqVO updateReqVO) {
        // 校验存在
        validateTeachClassExists(updateReqVO.getId());
        // 更新
        TeachClassDO updateObj = BeanUtils.toBean(updateReqVO, TeachClassDO.class);
        teachClassMapper.updateById(updateObj);
    }

    @Override
    public void updateTotalNumber(Long classId, int totalNumber) {
        TeachClassDO teachClass = teachClassMapper.selectById(classId);
        if (teachClass == null) {
            throw exception(TEACH_CLASS_NOT_EXISTS);
        }
        teachClass.setTotalNumber(totalNumber);
        teachClassMapper.updateById(teachClass);
    }

    @Override
    public void deleteTeachClass(Long id) {
        // 校验存在
        validateTeachClassExists(id);
        // 删除
        teachClassMapper.deleteById(id);
    }

    @Override
        public void deleteTeachClassListByIds(List<Long> ids) {
        // 删除
        teachClassMapper.deleteByIds(ids);
        }


    private void validateTeachClassExists(Long id) {
        if (teachClassMapper.selectById(id) == null) {
            throw exception(TEACH_CLASS_NOT_EXISTS);
        }
    }

    @Override
    public TeachClassDO getTeachClass(Long id) {
        return teachClassMapper.selectById(id);
    }

    @Override
    public PageResult<TeachClassDO> getTeachClassPage(TeachClassPageReqVO pageReqVO) {
        return teachClassMapper.selectPage(pageReqVO);
    }

}