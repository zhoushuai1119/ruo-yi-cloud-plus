package org.dromara.common.sms.core.dao;

import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.redisson.utils.RedissonUtil;
import org.dromara.sms4j.api.dao.SmsDao;

import java.time.Duration;

/**
 * SmsDao缓存配置 (使用框架自带RedisUtils实现 协议统一)
 * <p>主要用于短信重试和拦截的缓存
 *
 * @author shuai.zhou
 */
public class PlusSmsDao implements SmsDao {

    /**
     * 存储
     *
     * @param key       键
     * @param value     值
     * @param cacheTime 缓存时间（单位：秒)
     */
    @Override
    public void set(String key, Object value, long cacheTime) {
        RedissonUtil.setCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, Duration.ofSeconds(cacheTime));
    }

    /**
     * 存储
     *
     * @param key   键
     * @param value 值
     */
    @Override
    public void set(String key, Object value) {
        RedissonUtil.setCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key, value, true);
    }

    /**
     * 读取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public Object get(String key) {
        return RedissonUtil.getCacheObject(GlobalConstants.GLOBAL_REDIS_KEY + key);
    }

    /**
     * remove
     * <p> 根据key移除缓存
     *
     * @param key 缓存键
     * @return 被删除的value
     * @author :Wind
     */
    @Override
    public Object remove(String key) {
        return RedissonUtil.deleteObject(GlobalConstants.GLOBAL_REDIS_KEY + key);
    }

    /**
     * 清空
     */
    @Override
    public void clean() {
        RedissonUtil.deleteObject(GlobalConstants.GLOBAL_REDIS_KEY + "sms:");
    }

}
