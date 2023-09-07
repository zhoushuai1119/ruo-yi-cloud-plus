package org.dromara.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.common.redis.manager.PlusSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * redis配置
 *
 * @author Lion Li
 */
@Slf4j
@Configuration
public class RedisConfiguration {

    /**
     * 自定义缓存管理器 整合spring-cache
     */
    @Bean
    public CacheManager cacheManager() {
        return new PlusSpringCacheManager();
    }

}
