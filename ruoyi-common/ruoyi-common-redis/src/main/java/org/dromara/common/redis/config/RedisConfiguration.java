package org.dromara.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.dromara.common.redis.manager.PlusSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * redis配置
 *
 * @author Lion Li
 */
@Slf4j
@EnableCaching
@Configuration
@PropertySource(value = "classpath:common-redisson.yml", factory = YmlPropertySourceFactory.class)
public class RedisConfiguration {

    /**
     * 自定义缓存管理器 整合spring-cache
     */
    @Bean
    public CacheManager cacheManager() {
        return new PlusSpringCacheManager();
    }

}
