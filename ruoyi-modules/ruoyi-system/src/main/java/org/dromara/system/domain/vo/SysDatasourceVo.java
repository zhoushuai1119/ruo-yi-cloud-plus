package org.dromara.system.domain.vo;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import org.dromara.system.domain.SysDatasource;

import java.io.Serial;
import java.io.Serializable;



/**
 * 多数据源配置视图对象 sys_datasource_config
 *
 * @author LionLi
 * @date 2023-11-13
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = SysDatasource.class)
public class SysDatasourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 租户编号
     */
    @ExcelProperty(value = "租户编号")
    private String tenantId;

    /**
     * 数据源名称
     */
    @ExcelProperty(value = "数据源名称")
    private String name;

    /**
     * 数据库驱动
     */
    @ExcelProperty(value = "数据库驱动")
    private String driverClassName;

    /**
     * 数据库连接地址
     */
    @ExcelProperty(value = "数据库连接地址")
    private String url;

    /**
     * 数据库用户名
     */
    @ExcelProperty(value = "数据库用户名")
    private String userName;

    /**
     * 数据库密码
     */
    @ExcelProperty(value = "数据库密码")
    private String password;

}
