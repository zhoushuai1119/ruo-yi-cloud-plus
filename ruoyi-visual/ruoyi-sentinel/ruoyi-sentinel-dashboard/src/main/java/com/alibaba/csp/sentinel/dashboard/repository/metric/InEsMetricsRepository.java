package com.alibaba.csp.sentinel.dashboard.repository.metric;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.MetricEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.EsMetric;
import com.alibaba.csp.sentinel.dashboard.mapper.EsMetricMapper;
import com.alibaba.csp.sentinel.dashboard.util.SnowflakeUtil;
import com.alibaba.csp.sentinel.util.StringUtil;
import org.dromara.easyes.core.conditions.select.LambdaEsQueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2022/10/20 16:00
 * @version: v1
 */
@Component
public class InEsMetricsRepository implements MetricsRepository<MetricEntity> {

    private static final long MAX_METRIC_LIVE_TIME_MS = 1000 * 60 * 5;

    private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Resource
    private EsMetricMapper esMetricMapper;

    @Override
    public void save(MetricEntity entity) {
        if (entity == null || StringUtil.isBlank(entity.getApp())) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            esMetricMapper.insert(toPo(entity));
        } finally {
            readWriteLock.writeLock().unlock();
        }

    }

    @Override
    public void saveAll(Iterable<MetricEntity> metrics) {
        if (metrics == null) {
            return;
        }
        readWriteLock.writeLock().lock();
        try {
            List<EsMetric> metricList = new ArrayList<>();
            metrics.forEach(metric -> metricList.add(toPo(metric)));
            esMetricMapper.insertBatch(metricList);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }

    @Override
    public List<MetricEntity> queryByAppAndResourceBetween(String app, String resource,
                                                           long startTime, long endTime) {
        List<MetricEntity> results = new ArrayList<>();

        if (StringUtil.isBlank(app)) {
            return results;
        }

        readWriteLock.readLock().lock();
        try {
            EsMetric metric = new EsMetric();
            metric.setApp(app);
            metric.setResource(resource);
            LambdaEsQueryWrapper<EsMetric> queryWrapper = new LambdaEsQueryWrapper<>();
            queryWrapper.between("timestamp", startTime, endTime);
            List<EsMetric> metricList = esMetricMapper.selectList(queryWrapper);

            if (CollUtil.isEmpty(metricList)) {
                return results;
            }

            metricList.forEach(e -> results.add(toPo(e)));
            return results;
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    @Override
    public List<String> listResourcesOfApp(String app) {
        List<String> results = new ArrayList<>();
        if (StringUtil.isBlank(app)) {
            return results;
        }

        final long minTimeMs = System.currentTimeMillis() - MAX_METRIC_LIVE_TIME_MS;
        Map<String, MetricEntity> resourceCount = new ConcurrentHashMap<>(32);

        readWriteLock.readLock().lock();
        try {
            LambdaEsQueryWrapper<EsMetric> queryWrapper = new LambdaEsQueryWrapper<>();
            queryWrapper.eq("app", app);
            queryWrapper.ge("timestamp", minTimeMs);
            List<EsMetric> metricList = esMetricMapper.selectList(queryWrapper);

            List<MetricEntity> metricEntityList = new ArrayList<>();
            metricList.forEach(e -> metricEntityList.add(toPo(e)));

            if (CollUtil.isEmpty(metricEntityList)) {
                return results;
            }

            for (MetricEntity newEntity : metricEntityList) {
                String resource = newEntity.getResource();
                if (resourceCount.containsKey(resource)) {
                    MetricEntity oldEntity = resourceCount.get(resource);
                    oldEntity.addPassQps(newEntity.getPassQps());
                    oldEntity.addRtAndSuccessQps(newEntity.getRt(), newEntity.getSuccessQps());
                    oldEntity.addBlockQps(newEntity.getBlockQps());
                    oldEntity.addExceptionQps(newEntity.getExceptionQps());
                    oldEntity.addCount(1);
                } else {
                    resourceCount.put(resource, MetricEntity.copyOf(newEntity));
                }
            }
            // Order by last minute b_qps DESC.
            return resourceCount.entrySet()
                .stream()
                .sorted((o1, o2) -> {
                    MetricEntity e1 = o1.getValue();
                    MetricEntity e2 = o2.getValue();
                    int t = e2.getBlockQps().compareTo(e1.getBlockQps());
                    if (t != 0) {
                        return t;
                    }
                    return e2.getPassQps().compareTo(e1.getPassQps());
                })
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    private EsMetric toPo(MetricEntity metricEntity) {
        EsMetric esMetric = new EsMetric();
        BeanUtils.copyProperties(metricEntity, esMetric, EsMetric.class);
        esMetric.setId(SnowflakeUtil.generMetricId());
        return esMetric;
    }

    private MetricEntity toPo(EsMetric metric) {
        MetricEntity metricEntity = new MetricEntity();
        BeanUtils.copyProperties(metric, metricEntity, MetricEntity.class);
        return metricEntity;
    }
}
