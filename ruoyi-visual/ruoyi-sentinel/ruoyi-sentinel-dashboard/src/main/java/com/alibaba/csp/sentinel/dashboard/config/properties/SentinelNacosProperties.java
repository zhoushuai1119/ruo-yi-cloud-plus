package com.alibaba.csp.sentinel.dashboard.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * nacos属性配置类
 *
 * @author shuai.zhou
 */
@Data
@ConfigurationProperties(prefix = "sentinel.nacos")
public class SentinelNacosProperties {

    /**
     * nacos服务器地址
     */
    private String serverAddr;
    /**
     * nacos命名空间
     */
    private String namespace;
    /**
     * nacos group
     */
    private String groupId;
    /**
     * nacos username
     */
    private String username;
    /**
     * nacos password
     */
    private String password;

}
