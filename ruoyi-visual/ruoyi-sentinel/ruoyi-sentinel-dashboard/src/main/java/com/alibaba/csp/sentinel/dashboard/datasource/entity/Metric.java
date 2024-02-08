package com.alibaba.csp.sentinel.dashboard.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * Sentinel监控信息表
 * </p>
 *
 * @author zhoushuai
 * @since 2022-10-20
 */
@Data
@TableName("metric")
public class Metric implements Serializable {

    @Serial
    private static final long serialVersionUID = 7284545166372535082L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 应用名称
     */
    @TableField("app")
    private String app;
    /**
     * 资源名称
     */
    @TableField("resource")
    private String resource;
    /**
     * 监控信息时间戳
     */
    @TableField("timestamp")
    private Date timestamp;
    /**
     * 创建时间
     */
    @TableField("gmt_create")
    private Date gmtCreate;
    /**
     * 修改时间
     */
    @TableField("gmt_modified")
    private Date gmtModified;
    /**
     * 通过QPS
     */
    @TableField("pass_qps")
    private Long passQps;
    /**
     * 成功QPS
     */
    @TableField("success_qps")
    private Long successQps;
    /**
     * 限流QPS
     */
    @TableField("block_qps")
    private Long blockQps;
    /**
     * 异常QPS
     */
    @TableField("exception_qps")
    private Long exceptionQps;
    /**
     * 资源的平均响应时间
     */
    @TableField("rt")
    private BigDecimal rt;
    /**
     * 本次聚合的总条数
     */
    @TableField("count")
    private Integer count;
    /**
     * 资源hashcode
     */
    @TableField("resource_code")
    private Integer resourceCode;

}
