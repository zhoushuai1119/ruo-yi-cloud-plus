package org.dromara.resource.sms;

import com.cloud.lock.redisson.utils.RedisTemplateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/9/20 11:15
 * @version: v1
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SmsRedisDao implements SmsDao {

    private final RedisTemplateUtil redisTemplateUtil;

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    @Override
    public void set(String key, Object value, long cacheTime) {
        redisTemplateUtil.set(key, value, cacheTime, TimeUnit.SECONDS);
    }

    /**
     * 存储
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        redisTemplateUtil.set(key, value);
    }

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return redisTemplateUtil.get(key);
    }

    /**
     * 清空
     */
    @Override
    public void clean() {

    }

}
