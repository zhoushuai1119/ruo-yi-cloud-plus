package com.alibaba.cloud.sentinel.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * nacos配置类
 *
 * @author shuai.zhou
 */
@AutoConfiguration
@EnableConfigurationProperties({SentinelNacosProperties.class})
public class SentinelNacosConfig {

}
