package org.dromara.system.domain.bo;

import io.github.linpeilie.annotations.AutoMapper;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.encrypt.annotation.EncryptField;
import org.dromara.common.encrypt.enumd.AlgorithmType;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.system.domain.SysDatasource;

/**
 * 多数据源配置业务对象 sys_datasource_config
 *
 * @author LionLi
 * @date 2023-11-13
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = SysDatasource.class, reverseConvertGenerate = false)
public class SysDatasourceBo extends BaseEntity {

    /**
     * 主键自增ID
     */
    @NotNull(message = "主键自增ID不能为空", groups = { EditGroup.class })
    private Long id;

    /**
     * 租户编号
     */
    @NotBlank(message = "租户编号不能为空", groups = { AddGroup.class, EditGroup.class })
    private String tenantId;

    /**
     * 数据源名称
     */
    @NotBlank(message = "数据源名称不能为空", groups = { AddGroup.class, EditGroup.class })
    private String name;

    /**
     * 数据库驱动
     */
    @NotBlank(message = "数据库驱动不能为空", groups = { AddGroup.class, EditGroup.class })
    private String driverClassName;

    /**
     * 数据库连接地址
     */
    @NotBlank(message = "数据库连接地址不能为空", groups = { AddGroup.class, EditGroup.class })
    private String url;

    /**
     * 数据库用户名
     */
    @NotBlank(message = "数据库用户名不能为空", groups = { AddGroup.class, EditGroup.class })
    private String userName;

    /**
     * 数据库密码
     */
    @NotBlank(message = "数据库密码不能为空", groups = { AddGroup.class, EditGroup.class })
    @EncryptField(algorithm = AlgorithmType.AES, password = "10rfylhtccpuyke5")
    private String password;

}
