package org.dromara.common.redis.config;

import org.dromara.common.redis.utils.ObjectMapperUtil;
import org.dromara.common.redis.utils.RedisTemplateUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @description: Redis序列化配置
 * @author: shuai.zhou
 * @date: 2020/12/29 20:42
 * @version: V1.0
 */
@Configuration
@ConditionalOnClass(RedisTemplate.class)
@Import(RedisTemplateUtil.class)
public class RedisAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisCacheTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // key序列化: String
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // 使用Jackson2JsonRedisSerialize替换默认序列化; 主要为解决LocalDataTime序列化问题
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(ObjectMapperUtil.OBJECT_MAPPER, Object.class);

        // value序列化方式采用Jackson2JsonRedisSerializer
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        // 开启事务
        template.setEnableTransactionSupport(true);
        template.afterPropertiesSet();
        return template;
    }

}
