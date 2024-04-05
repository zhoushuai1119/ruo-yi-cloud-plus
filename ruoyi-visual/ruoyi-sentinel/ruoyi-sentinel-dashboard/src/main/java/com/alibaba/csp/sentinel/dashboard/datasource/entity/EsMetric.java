package com.alibaba.csp.sentinel.dashboard.datasource.entity;

import lombok.Data;
import org.dromara.easyes.annotation.IndexField;
import org.dromara.easyes.annotation.IndexId;
import org.dromara.easyes.annotation.IndexName;
import org.dromara.easyes.annotation.rely.FieldType;
import org.dromara.easyes.annotation.rely.IdType;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * Sentinel监控信息
 * </p>
 *
 * @author zhoushuai
 * @since 2022-10-20
 */
@Data
@IndexName("sentinel_metric")
public class EsMetric implements Serializable {

    @Serial
    private static final long serialVersionUID = 7284545166372535082L;

    /**
     * id
     */
    @IndexId(type = IdType.CUSTOMIZE)
    private Long id;
    /**
     * 应用名称
     */
    @IndexField(value = "app")
    private String app;
    /**
     * 资源名称
     */
    @IndexField(value = "resource")
    private String resource;
    /**
     * 通过QPS
     */
    @IndexField(value = "pass_qps", fieldType = FieldType.LONG)
    private Long passQps;
    /**
     * 成功QPS
     */
    @IndexField(value = "success_qps", fieldType = FieldType.LONG)
    private Long successQps;
    /**
     * 限流QPS
     */
    @IndexField(value = "block_qps", fieldType = FieldType.LONG)
    private Long blockQps;
    /**
     * 异常QPS
     */
    @IndexField(value = "exception_qps", fieldType = FieldType.LONG)
    private Long exceptionQps;
    /**
     * 资源的平均响应时间
     */
    @IndexField(value = "rt", fieldType = FieldType.DOUBLE)
    private double rt;
    /**
     * 本次聚合的总条数
     */
    @IndexField(value = "count", fieldType = FieldType.INTEGER)
    private int count;
    /**
     * 资源hashcode
     */
    @IndexField(value = "resource_code", fieldType = FieldType.INTEGER)
    private int resourceCode;
    /**
     * 监控信息时间戳
     */
    @IndexField(value = "timestamp", fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;
    /**
     * 创建时间
     */
    @IndexField(value = "gmt_create", fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date gmtCreate;
    /**
     * 修改时间
     */
    @IndexField(value = "gmt_modified", fieldType = FieldType.DATE, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date gmtModified;

}
