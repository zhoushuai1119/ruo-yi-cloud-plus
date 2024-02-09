package org.dromara.common.sentinel.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * sentinel 配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-sentinel.yml", factory = YmlPropertySourceFactory.class)
public class SentinelConfiguration {

}
