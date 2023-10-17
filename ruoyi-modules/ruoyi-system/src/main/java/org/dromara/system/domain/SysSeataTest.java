package org.dromara.system.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 资源Seata测试 sys_seata_test
 *
 * @author Michelle.Chung
 */
@Data
@TableName("sys_seata_test")
public class SysSeataTest implements Serializable {

    @Serial
    private static final long serialVersionUID = 5958120255359072281L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户编号
     */
    @TableField("tenant_id")
    private String tenantId;

    /**
     * value
     */
    @TableField("value")
    private String value;

}