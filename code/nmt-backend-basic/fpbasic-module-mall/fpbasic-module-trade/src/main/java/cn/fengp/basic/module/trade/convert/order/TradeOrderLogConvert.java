package cn.fengp.basic.module.trade.convert.order;

import cn.fengp.basic.module.trade.dal.dataobject.order.TradeOrderLogDO;
import cn.fengp.basic.module.trade.service.order.bo.TradeOrderLogCreateReqBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TradeOrderLogConvert {

    TradeOrderLogConvert INSTANCE = Mappers.getMapper(TradeOrderLogConvert.class);

    TradeOrderLogDO convert(TradeOrderLogCreateReqBO bean);

}
