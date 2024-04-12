package org.dromara.common.redisson.utils;

import com.alibaba.fastjson2.JSON;
import jodd.util.StringUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作工具类
 *
 * @author shuai.zhou
 */
@RequiredArgsConstructor
public class RedisTemplateUtil {

    private final RedisTemplate<String, Object> redisTemplate;

    // =============================common============================

    /**
     * @description: 指定缓存失效时间
     * @author: zhou shuai
     * @date: 2023/4/25 14:23
     * @param: key key值
     * @param: time 时间
     * @param: timeUnit 时间单位
     * @return: boolean
     */
    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 指定缓存失效时间
     * @author: zhou shuai
     * @date: 2023/4/25 14:23
     * @param: key key值
     * @param: time 时间(默认秒)
     * @return: boolean
     */
    public boolean expire(String key, long time) {
        return expire(key, time, TimeUnit.SECONDS);
    }

    /**
     * @description: 获取key过期时间
     * @author: zhou shuai
     * @date: 2023/4/25 14:27
     * @param: key
     * @param: timeUnit
     * @return: long
     */
    public Long getExpire(String key, TimeUnit timeUnit) {
        return redisTemplate.getExpire(key, timeUnit);
    }

    /**
     * @description: 获取key过期时间(单位 : 秒)
     * @author: zhou shuai
     * @date: 2023/4/25 14:27
     * @param: key
     * @return: long
     */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    /**
     * @description: 判断key是否存在
     * @author: zhou shuai
     * @date: 2023/4/25 14:30
     * @param: key
     * @return: boolean
     */
    public boolean hasKey(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            return false;
        }
    }

    //============================String=============================

    /**
     * @description: 普通缓存获取
     * @author: zhou shuai
     * @date: 2023/4/25 14:38
     * @param: key
     * @return: java.lang.Object
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * @description: 普通缓存获取指定类型
     * @author: zhou shuai
     * @date: 2023/4/25 14:42
     * @param: key
     * @param: t
     * @return: T
     */
    public <T> T get(String key, Class<T> t) {
        Object result = key == null ? null : redisTemplate.opsForValue().get(key);
        String resultStr = result != null ? JSON.toJSONString(result) : null;
        return StringUtil.isNotEmpty(resultStr) ? JSON.parseObject(resultStr, t) : null;
    }

    /**
     * @description: 普通缓存获取指定类型
     * @author: zhou shuai
     * @date: 2023/4/25 14:42
     * @param: key
     * @param: t
     * @return: T
     */
    public <T> List<T> getArray(String key, Class<T> t) {
        Object result = key == null ? null : redisTemplate.opsForValue().get(key);
        String resultStr = result != null ? JSON.toJSONString(result) : null;
        return StringUtil.isNotEmpty(resultStr) ? JSON.parseArray(resultStr, t) : new ArrayList<>();
    }

    /**
     * @description: 普通缓存放入
     * @author: zhou shuai
     * @date: 2023/4/25 14:42
     * @param: key
     * @param: value
     * @return: boolean
     */
    public boolean set(String key, Object value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * @description: 普通缓存放入并设置时间
     * @author: zhou shuai
     * @date: 2023/4/25 14:44
     * @param: key 键
     * @param: value 值
     * @param: time 时间time要大于0 如果time小于等于0 将设置无限期
     * @param: timeUnit 时间单位
     * @return: boolean
     */
    public boolean set(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, timeUnit);
            } else {
                set(key, value);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 普通缓存放入并设置时间
     * @author: zhou shuai
     * @date: 2023/4/25 14:44
     * @param: key 键
     * @param: value 值
     * @param: time 时间time要大于0 如果time小于等于0 将设置无限期; 时间单位默认为秒
     * @return: boolean
     */
    public boolean set(String key, Object value, long time) {
        return set(key, value, time, TimeUnit.SECONDS);
    }

    /**
     * @description: 递增
     * @author: zhou shuai
     * @date: 2023/4/25 14:47
     * @param: key
     * @param: delta 要增加几(大于0)
     * @return: long
     */
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * @description: 递增+1
     * @author: zhou shuai
     * @date: 2023/4/25 14:47
     * @param: key
     * @return: long
     */
    public long incr(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * @description: 递减
     * @author: zhou shuai
     * @date: 2023/4/25 14:49
     * @param: key
     * @param: delta 要减少几
     * @return: long
     */
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * @description: 递减-1
     * @author: zhou shuai
     * @date: 2023/4/25 14:49
     * @param: key
     * @return: long
     */
    public long decr(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    //================================Map=================================

    /**
     * @description: HashGet
     * @author: zhou shuai
     * @date: 2023/4/25 20:03
     * @param: key
     * @param: item
     * @return: java.lang.Object
     */
    public Object hget(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    /**
     * @description: 获取hashKey对应的所有键值
     * @author: zhou shuai
     * @date: 2023/4/25 20:03
     * @param: key
     * @return: java.util.Map<java.lang.Object, java.lang.Object>
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * @description: HashSet
     * @author: zhou shuai
     * @date: 2023/4/25 20:04
     * @param: key
     * @param: map
     * @return: boolean
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: HashSet 并设置时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:35
     * @param: key
     * @param: map
     * @param: time 默认为秒
     * @return: boolean
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: HashSet 并设置时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:35
     * @param: key
     * @param: map
     * @param: time
     * @param: timeUnit 时间单位
     * @return: boolean
     */
    public boolean hmset(String key, Map<String, Object> map, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向一张hash表中放入数据, 如果不存在将创建
     * @author: zhou shuai
     * @date: 2023/4/26 16:43
     * @param: key
     * @param: item
     * @param: value
     * @return: boolean
     */
    public boolean hset(String key, String item, Object value) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向一张hash表中放入数据, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:43
     * @param: key
     * @param: item
     * @param: value
     * @param: time 默认为秒
     * @return: boolean
     */
    public boolean hset(String key, String item, Object value, long time) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向一张hash表中放入数据, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:43
     * @param: key
     * @param: item
     * @param: value
     * @param: time
     * @param: timeUnit 时间单位
     * @return: boolean
     */
    public boolean hset(String key, String item, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForHash().put(key, item, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 删除hash表中的值
     * @author: zhou shuai
     * @date: 2023/4/26 16:45
     * @param: key
     * @param: item
     */
    public void hdel(String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * @description: 判断hash表中是否有该项的值
     * @author: zhou shuai
     * @date: 2023/4/26 16:47
     * @param: key
     * @param: item
     * @return: boolean
     */
    public boolean hHasKey(String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    //============================set=============================

    /**
     * @description: 根据key获取Set中的所有值
     * @author: zhou shuai
     * @date: 2023/4/26 16:50
     * @param: key
     * @return: java.util.Set<java.lang.Object>
     */
    public Set<Object> sGet(String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @description: 根据value从一个set中查询, 是否存在
     * @author: zhou shuai
     * @date: 2023/4/26 16:50
     * @param: key
     * @param: value
     * @return: boolean
     */
    public boolean sHasKey(String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 将数据放入set缓存
     * @author: zhou shuai
     * @date: 2023/4/26 16:50
     * @param: key
     * @param: values
     * @return: long
     */
    public long sSet(String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description: 将数据放入set缓存, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:51
     * @param: key
     * @param: time
     * @param: values
     * @return: long
     */
    public long sSetAndTime(String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time);
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description: 将数据放入set缓存, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:51
     * @param: key
     * @param: time
     * @param: values
     * @return: long
     */
    public long sSetAndTime(String key, long time, TimeUnit timeUnit, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description: 获取set缓存的长度
     * @author: zhou shuai
     * @date: 2023/4/26 16:53
     * @param: key
     * @return: long
     */
    public long sGetSetSize(String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description: 移除值为value的
     * @author: zhou shuai
     * @date: 2023/4/26 16:53
     * @param: key
     * @param: values
     * @return: long
     */
    public long setRemove(String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            return 0;
        }
    }

    //===============================list=================================

    /**
     * @description: 获取list缓存的内容
     * @author: zhou shuai
     * @date: 2023/4/26 16:54
     * @param: key
     * @param: start
     * @param: end
     * @return: java.util.List<java.lang.Object>
     */
    public List<Object> lGet(String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @description: 获取list缓存的长度
     * @author: zhou shuai
     * @date: 2023/4/26 16:54
     * @param: key
     * @return: long
     */
    public long lGetListSize(String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * @description: 通过索引 获取list中的值
     * @author: zhou shuai
     * @date: 2023/4/26 16:55
     * @param: key
     * @param: index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return: java.lang.Object
     */
    public Object lGetIndex(String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * @description: 向list放入数据
     * @author: zhou shuai
     * @date: 2023/4/26 16:56
     * @param: key
     * @param: value
     * @return: boolean
     */
    public boolean lSet(String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向list放入数据，并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:56
     * @param: key
     * @param: value
     * @param: time 默认为秒
     * @return: boolean
     */
    public boolean lSet(String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向list放入数据，并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:56
     * @param: key
     * @param: value
     * @param: time
     * @param: timeUnit 时间单位
     * @return: boolean
     */
    public boolean lSet(String key, Object value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向list放入多个数据
     * @author: zhou shuai
     * @date: 2023/4/26 16:58
     * @param: key
     * @param: value
     * @return: boolean
     */
    public boolean lSet(String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向list放入多个数据, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:58
     * @param: key
     * @param: value
     * @param: time 默认为秒
     * @return: boolean
     */
    public boolean lSet(String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 向list放入多个数据, 并设置过期时间
     * @author: zhou shuai
     * @date: 2023/4/26 16:58
     * @param: key
     * @param: value
     * @param: time 默认为秒
     * @return: boolean
     */
    public boolean lSet(String key, List<Object> value, long time, TimeUnit timeUnit) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 根据索引修改list中的某条数据
     * @author: zhou shuai
     * @date: 2023/4/26 16:59
     * @param: key
     * @param: index
     * @param: value
     * @return: boolean
     */
    public boolean lUpdateIndex(String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * @description: 移除N个值为value
     * @author: zhou shuai
     * @date: 2023/4/26 17:00
     * @param: key
     * @param: count
     * @param: value
     * @return: long
     */
    public long lRemove(String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList().remove(key, count, value);
            return remove;
        } catch (Exception e) {
            return 0;
        }
    }

}
