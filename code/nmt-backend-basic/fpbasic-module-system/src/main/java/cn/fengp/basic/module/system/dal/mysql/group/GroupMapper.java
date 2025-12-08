package cn.fengp.basic.module.system.dal.mysql.group;

import java.util.*;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.fengp.basic.framework.mybatis.core.mapper.BaseMapperX;
import cn.fengp.basic.module.system.dal.dataobject.group.GroupDO;
import org.apache.ibatis.annotations.Mapper;
import cn.fengp.basic.module.system.controller.admin.group.vo.*;

/**
 * 用户组 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface GroupMapper extends BaseMapperX<GroupDO> {

    default PageResult<GroupDO> selectPage(GroupPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<GroupDO>()
                .likeIfPresent(GroupDO::getName, reqVO.getName())
                .eqIfPresent(GroupDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(GroupDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(GroupDO::getId));
    }

}