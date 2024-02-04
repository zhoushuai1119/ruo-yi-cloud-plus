package org.dromara.resource.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 资源Seata测试 res_seata_test
 *
 * @author shuai.zhou
 */
@Data
@TableName("res_seata_test")
public class ResSeataTest implements Serializable {

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
