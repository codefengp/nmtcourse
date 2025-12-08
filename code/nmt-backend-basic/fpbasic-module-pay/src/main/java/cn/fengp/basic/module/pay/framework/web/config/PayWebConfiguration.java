package cn.fengp.basic.module.pay.framework.web.config;

import cn.fengp.basic.framework.swagger.config.FpbasicSwaggerAutoConfiguration;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * pay 模块的 web 组件的 Configuration
 *
 * @author 芋道源码
 */
@Configuration(proxyBeanMethods = false)
public class PayWebConfiguration {

    /**
     * pay 模块的 API 分组
     */
    @Bean
    public GroupedOpenApi payGroupedOpenApi() {
        return FpbasicSwaggerAutoConfiguration.buildGroupedOpenApi("pay");
    }

}
