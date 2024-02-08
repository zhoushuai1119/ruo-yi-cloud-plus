package com.cloud.sentinel.token.server.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * apollo属性配置类
 *
 * @author shuai.zhou
 */
@Data
@ConfigurationProperties(prefix = "apollo")
public class ApolloProperties {

    /**
     * app.id
     */
    private String appId;
    /**
     * meta地址
     */
    private String meta;
    /**
     * 第三方授权token
     */
    private String token;
    /**
     * 管理员名称
     */
    private String user;
    /**
     * 集群名称
     */
    private String clusterName;
    /**
     * 限流规则 namespace
     */
    private String namespace;

}
