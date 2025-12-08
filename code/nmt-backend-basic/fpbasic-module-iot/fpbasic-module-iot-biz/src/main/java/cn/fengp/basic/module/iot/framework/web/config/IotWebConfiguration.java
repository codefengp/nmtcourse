package cn.fengp.basic.module.iot.framework.web.config;

import cn.fengp.basic.framework.swagger.config.FpbasicSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * iot 模块的 web 组件的 Configuration
 *
 * @author ahh
 */
@Configuration(proxyBeanMethods = false)
public class IotWebConfiguration {

    /**
     * iot 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi iotGroupedOpenApi() {
        return FpbasicSwaggerAutoConfiguration.buildGroupedOpenApi("iot");
    }

}
