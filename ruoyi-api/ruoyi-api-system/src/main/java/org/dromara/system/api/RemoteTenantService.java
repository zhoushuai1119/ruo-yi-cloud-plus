package org.dromara.system.api;


import org.dromara.system.api.domain.vo.RemoteTenantDataSourceVo;
import org.dromara.system.api.domain.vo.RemoteTenantVo;

import java.util.List;

/**
 * @author zhujie
 */
public interface RemoteTenantService {

    /**
     * 根据租户id获取租户详情
     *
     * @param tenantId 租户id
     * @return 结果
     */
    RemoteTenantVo queryByTenantId(String tenantId);

    /**
     * 根据租户id获取租户配置的数据源
     *
     * @param tenantId 租户id
     * @return 结果
     */
    RemoteTenantDataSourceVo queryDataSourceByTenantId(String tenantId);

    /**
     * 获取租户列表
     *
     * @return 结果
     */
    List<RemoteTenantVo> queryList();

}
