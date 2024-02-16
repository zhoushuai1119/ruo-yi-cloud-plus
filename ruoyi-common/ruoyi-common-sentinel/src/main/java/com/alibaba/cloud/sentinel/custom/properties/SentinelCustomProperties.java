package com.alibaba.cloud.sentinel.custom.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * sentinel自定义配置类
 *
 * @author shuai.zhou
 */
@Data
@ConfigurationProperties(prefix = "spring.cloud.sentinel.transport")
public class SentinelCustomProperties {

    /**
     * sentinel-dashboard服务名
     */
    private String serverName;

}
