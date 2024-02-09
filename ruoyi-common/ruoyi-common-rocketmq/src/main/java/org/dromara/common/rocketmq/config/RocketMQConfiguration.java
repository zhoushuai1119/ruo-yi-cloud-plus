package org.dromara.common.rocketmq.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * RocketMQ 配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-rocketmq.yml", factory = YmlPropertySourceFactory.class)
public class RocketMQConfiguration {

}
