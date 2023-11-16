package org.dromara.common.redis.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.dromara.common.redis.manager.PlusSpringCacheManager;
import org.redisson.api.RedissonClient;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * redisson 配置
 *
 * @author shuai.zhou
 */
@EnableCaching
@Configuration
@PropertySource(value = "classpath:common-redisson.yml", factory = YmlPropertySourceFactory.class)
public class RedisConfiguration {

    /**
     * 自定义缓存管理器 整合spring-cache
     */
    @Bean
    public CacheManager cacheManager(RedissonClient redissonClient) {
        return new PlusSpringCacheManager(redissonClient);
    }

}
