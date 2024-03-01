package org.dromara.monitor.rocketmq.config;

import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.acl.common.SessionCredentials;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shuai.zhou
 */
@Configuration
@EnableConfigurationProperties(MonitorRocketMQProperties.class)
public class MonitorConfig {

    @Bean
    public AclClientRPCHook aclRPCHook(MonitorRocketMQProperties monitorRocketMQProperties) {
        return new AclClientRPCHook(new SessionCredentials(monitorRocketMQProperties.getAccessKey(), monitorRocketMQProperties.getSecretKey()));
    }

}
