package org.dromara.common.prometheus.config;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * prometheus 配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration
public class PrometheusConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> configurer(@Value("${spring.application.name:app}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }

}
