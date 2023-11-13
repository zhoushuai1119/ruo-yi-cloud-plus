package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.tenant.core.TenantEntity;

import java.io.Serial;

/**
 * 多数据源配置对象 sys_datasource_config
 *
 * @author LionLi
 * @date 2023-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_datasource_config")
public class SysDatasource extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 5141924099081833672L;

    /**
     * 主键自增ID
     */
    @TableId(value = "id")
    private Long id;

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
