package cn.fengp.basic.module.trade.controller.app.brokerage;

import cn.fengp.basic.framework.common.pojo.CommonResult;
import cn.fengp.basic.framework.common.pojo.PageResult;
import cn.fengp.basic.framework.common.util.object.BeanUtils;
import cn.fengp.basic.framework.dict.core.DictFrameworkUtils;
import cn.fengp.basic.module.trade.controller.admin.brokerage.vo.record.BrokerageRecordPageReqVO;
import cn.fengp.basic.module.trade.controller.app.brokerage.vo.record.AppBrokerageProductPriceRespVO;
import cn.fengp.basic.module.trade.controller.app.brokerage.vo.record.AppBrokerageRecordPageReqVO;
import cn.fengp.basic.module.trade.controller.app.brokerage.vo.record.AppBrokerageRecordRespVO;
import cn.fengp.basic.module.trade.dal.dataobject.brokerage.BrokerageRecordDO;
import cn.fengp.basic.module.trade.enums.DictTypeConstants;
import cn.fengp.basic.module.trade.service.brokerage.BrokerageRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static cn.fengp.basic.framework.common.pojo.CommonResult.success;
import static cn.fengp.basic.framework.web.core.util.WebFrameworkUtils.getLoginUserId;

@Tag(name = "用户 APP - 分销用户")
@RestController
@RequestMapping("/trade/brokerage-record")
@Validated
@Slf4j
public class AppBrokerageRecordController {

    @Resource
    private BrokerageRecordService brokerageRecordService;

    @GetMapping("/page")
    @Operation(summary = "获得分销记录分页")
    public CommonResult<PageResult<AppBrokerageRecordRespVO>> getBrokerageRecordPage(@Valid AppBrokerageRecordPageReqVO pageReqVO) {
        PageResult<BrokerageRecordDO> pageResult = brokerageRecordService.getBrokerageRecordPage(
                BeanUtils.toBean(pageReqVO, BrokerageRecordPageReqVO.class).setUserId(getLoginUserId()));
        return success(BeanUtils.toBean(pageResult, AppBrokerageRecordRespVO.class, recordVO ->
                recordVO.setStatusName(DictFrameworkUtils.parseDictDataLabel(DictTypeConstants.BROKERAGE_RECORD_STATUS, recordVO.getStatus()))));
    }

    @GetMapping("/get-product-brokerage-price")
    @Operation(summary = "获得商品的分销金额")
    public CommonResult<AppBrokerageProductPriceRespVO> getProductBrokeragePrice(@RequestParam("spuId") Long spuId) {
        return success(brokerageRecordService.calculateProductBrokeragePrice(getLoginUserId(), spuId));
    }

}