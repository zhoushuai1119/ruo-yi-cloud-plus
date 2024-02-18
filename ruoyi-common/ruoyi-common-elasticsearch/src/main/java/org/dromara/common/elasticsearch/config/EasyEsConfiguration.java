package org.dromara.common.elasticsearch.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * easy-es 配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-elasticsearch.yml", factory = YmlPropertySourceFactory.class)
public class EasyEsConfiguration {

}
