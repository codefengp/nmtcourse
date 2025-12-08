package cn.fengp.basic.module.member.convert.tag;

import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.module.member.controller.admin.tag.vo.MemberTagCreateReqVO;
import cn.fengp.basic.module.member.controller.admin.tag.vo.MemberTagRespVO;
import cn.fengp.basic.module.member.controller.admin.tag.vo.MemberTagUpdateReqVO;
import cn.fengp.basic.module.member.dal.dataobject.tag.MemberTagDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 会员标签 Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface MemberTagConvert {

    MemberTagConvert INSTANCE = Mappers.getMapper(MemberTagConvert.class);

    MemberTagDO convert(MemberTagCreateReqVO bean);

    MemberTagDO convert(MemberTagUpdateReqVO bean);

    MemberTagRespVO convert(MemberTagDO bean);

    List<MemberTagRespVO> convertList(List<MemberTagDO> list);

    PageResult<MemberTagRespVO> convertPage(PageResult<MemberTagDO> page);

}
