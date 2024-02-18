package com.alibaba.csp.sentinel.dashboard.util;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;

/**
 * @author: zhou shuai
 * @date: 2024/2/18 21:23
 * @version: v1
 */
public class SnowflakeUtil {

    /**
     * 生成EsMetric主键ID(保证唯一性)
     */
    public static long generMetricId() {
        Snowflake snowflake = IdUtil.getSnowflake(1, 1);
        long snowflakeId = snowflake.nextId();
        long metricId = snowflakeId + RandomUtil.randomLong(10000, 100000);
        return metricId;
    }

}
