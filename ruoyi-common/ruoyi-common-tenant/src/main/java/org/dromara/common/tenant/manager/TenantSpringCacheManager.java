package org.dromara.common.tenant.manager;

import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.redis.manager.PlusSpringCacheManager;
import org.dromara.common.tenant.helper.TenantHelper;
import org.springframework.cache.Cache;

/**
 * 重写 cacheName 处理方法 支持多租户
 *
 * @author shuai.zhou
 */
public class TenantSpringCacheManager extends PlusSpringCacheManager {

    public TenantSpringCacheManager() {
        super();
    }

    @Override
    public Cache getCache(String name) {
        if (StringUtils.contains(name, GlobalConstants.GLOBAL_REDIS_KEY)) {
            return super.getCache(name);
        }
        String tenantId = TenantHelper.getTenantId();
        if (StringUtils.startsWith(name, tenantId)) {
            // 如果存在则直接返回
            return super.getCache(name);
        }
        return super.getCache(tenantId + ":" + name);
    }

}
