package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * @author: zhou shuai
 * @description: 公共属性 Entity
 * @date: 2022/10/22 10:04
 */
@Data
public class CommonEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4554560495647163902L;

    private Long id;
    private String app;
    private String ip;
    private Integer port;
    /**
     * 表示要限制来自哪些来源的调用，default是全部都限制
     */
    private String limitApp;
    /**
     * 资源名称
     */
    private String resource;
    private Date gmtCreate;
    private Date gmtModified;

}
