package org.dromara.common.arthas.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.PropertySource;

/**
 * Arthas 配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration
@PropertySource(value = "classpath:common-arthas.yml", factory = YmlPropertySourceFactory.class)
public class ArthasConfiguration {

}
