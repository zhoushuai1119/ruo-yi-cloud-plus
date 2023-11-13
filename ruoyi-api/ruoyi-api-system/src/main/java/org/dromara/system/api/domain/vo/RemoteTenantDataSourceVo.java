package org.dromara.system.api.domain.vo;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;


/**
 * 租户视图对象
 *
 * @author zhujie
 */
@Data
public class RemoteTenantDataSourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 租户编号
     */
    private String tenantId;

    /**
     * 数据源名称
     */
    private String name;

    /**
     * 数据库驱动
     */
    private String driverClassName;

    /**
     * 数据库连接地址
     */
    private String url;

    /**
     * 数据库用户名
     */
    private String userName;

    /**
     * 数据库密码
     */
    private String password;

}
