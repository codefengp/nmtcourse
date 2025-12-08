package cn.fengp.basic.module.promotion.job.combination;

import cn.hutool.core.util.StrUtil;
import cn.fengp.basic.framework.common.core.KeyValue;
import cn.fengp.basic.framework.quartz.core.handler.JobHandler;
import cn.fengp.basic.framework.tenant.core.job.TenantJob;
import cn.fengp.basic.module.promotion.service.combination.CombinationRecordService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 拼团过期 Job
 *
 * @author HUIHUI
 */
@Component
public class CombinationRecordExpireJob implements JobHandler {

    @Resource
    private CombinationRecordService combinationRecordService;

    @Override
    @TenantJob
    public String execute(String param) {
        KeyValue<Integer, Integer> keyValue = combinationRecordService.expireCombinationRecord();
        return StrUtil.format("过期拼团 {} 个, 虚拟成团 {} 个", keyValue.getKey(), keyValue.getValue());
    }

}
