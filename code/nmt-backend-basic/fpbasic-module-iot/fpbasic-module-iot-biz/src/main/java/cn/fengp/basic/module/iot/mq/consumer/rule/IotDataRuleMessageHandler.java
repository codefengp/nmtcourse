package cn.fengp.basic.module.iot.mq.consumer.rule;

import cn.fengp.basic.framework.tenant.core.util.TenantUtils;
import cn.fengp.basic.module.iot.core.messagebus.core.IotMessageBus;
import cn.fengp.basic.module.iot.core.messagebus.core.IotMessageSubscriber;
import cn.fengp.basic.module.iot.core.mq.message.IotDeviceMessage;
import cn.fengp.basic.module.iot.service.rule.data.IotDataRuleService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 针对 {@link IotDeviceMessage} 的消费者，处理数据流转
 *
 * @author 芋道源码
 */
@Component
@Slf4j
public class IotDataRuleMessageHandler implements IotMessageSubscriber<IotDeviceMessage> {

    @Resource
    private IotDataRuleService dataRuleService;

    @Resource
    private IotMessageBus messageBus;

    @PostConstruct
    public void init() {
        messageBus.register(this);
    }

    @Override
    public String getTopic() {
        return IotDeviceMessage.MESSAGE_BUS_DEVICE_MESSAGE_TOPIC;
    }

    @Override
    public String getGroup() {
        return "iot_data_rule_consumer";
    }

    @Override
    public void onMessage(IotDeviceMessage message) {
        TenantUtils.execute(message.getTenantId(), () -> dataRuleService.executeDataRule(message));
    }

}
