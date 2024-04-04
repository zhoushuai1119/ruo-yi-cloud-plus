package com.cloud.sentinel.token.server.config;

import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.cloud.sentinel.token.server.config.properties.SentinelNacosProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * nacos配置类
 *
 * @author shuai.zhou
 */
@Configuration
@EnableConfigurationProperties({SentinelNacosProperties.class})
public class SentinelNacosConfig {

    @Bean
    public ConfigService nacosConfigService(SentinelNacosProperties sentinelNacosProperties) throws Exception {
        Properties properties = new Properties();
        // 这里在创建ConfigService实例时加了Nacos实例地址和命名空间两个属性。
        properties.put(PropertyKeyConst.SERVER_ADDR, sentinelNacosProperties.getServerAddr());
        properties.put(PropertyKeyConst.NAMESPACE, sentinelNacosProperties.getNamespace());
        properties.put(PropertyKeyConst.USERNAME, sentinelNacosProperties.getUsername());
        properties.put(PropertyKeyConst.PASSWORD, sentinelNacosProperties.getPassword());
        return ConfigFactory.createConfigService(properties);
    }

}
