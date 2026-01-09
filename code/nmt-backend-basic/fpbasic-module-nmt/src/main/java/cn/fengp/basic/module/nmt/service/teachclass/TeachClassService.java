package cn.fengp.basic.module.nmt.service.teachclass;

import java.util.*;
import jakarta.validation.*;
import cn.fengp.basic.module.nmt.controller.admin.teachclass.vo.*;
import cn.fengp.basic.module.nmt.dal.dataobject.teachclass.TeachClassDO;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.pojo.PageParam;

/**
 * 教学班级 Service 接口
 *
 * @author fengpeng
 */
public interface TeachClassService {

    /**
     * 创建教学班级
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createTeachClass(@Valid TeachClassSaveReqVO createReqVO);

    /**
     * 更新教学班级
     *
     * @param updateReqVO 更新信息
     */
    void updateTeachClass(@Valid TeachClassSaveReqVO updateReqVO);

    /**
     * 删除教学班级
     *
     * @param id 编号
     */
    void deleteTeachClass(Long id);

    /**
    * 批量删除教学班级
    *
    * @param ids 编号
    */
    void deleteTeachClassListByIds(List<Long> ids);

    /**
     * 获得教学班级
     *
     * @param id 编号
     * @return 教学班级
     */
    TeachClassDO getTeachClass(Long id);

    /**
     * 获得教学班级分页
     *
     * @param pageReqVO 分页查询
     * @return 教学班级分页
     */
    PageResult<TeachClassDO> getTeachClassPage(TeachClassPageReqVO pageReqVO);

}