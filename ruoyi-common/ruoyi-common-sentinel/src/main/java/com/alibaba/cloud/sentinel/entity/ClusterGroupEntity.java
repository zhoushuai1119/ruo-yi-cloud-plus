package com.alibaba.cloud.sentinel.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author zhoushuai
 */
@Data
public class ClusterGroupEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4548223899553986703L;

    /**
     * token server ip
     */
    private String ip;

    /**
     * token server port
     */
    private Integer port;

    /**
     * max allowed qps
     */
    private double maxAllowedQps;

}
