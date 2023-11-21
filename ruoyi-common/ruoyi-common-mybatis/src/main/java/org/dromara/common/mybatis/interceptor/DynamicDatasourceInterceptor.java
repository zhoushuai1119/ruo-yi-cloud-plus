package org.dromara.common.mybatis.interceptor;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.TenantConstants;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteTenantService;
import org.dromara.system.api.domain.vo.RemoteTenantDataSourceVo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

import static org.dromara.common.core.constant.GlobalConstants.DEFAULT_TENANT_SOURCE;

/**
 * @author: zhou shuai
 * @date: 2023/11/13 22:23
 * @version: v1
 */
@Slf4j
public class DynamicDatasourceInterceptor implements HandlerInterceptor {

    @DubboReference
    private RemoteTenantService remoteTenantService;

    /**
     * 服务名白名单
     */
    private final static List<String> EXCLUDE_APPLICATION_NAME_LIST = List.of("ruoyi-demo");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String applicationName = SpringUtil.getApplicationName();
        String tenantId = LoginHelper.getTenantId();
        if (EXCLUDE_APPLICATION_NAME_LIST.contains(applicationName) || StrUtil.isBlank(tenantId)) {
            return true;
        }
        boolean isSuperTenant = Objects.equals(tenantId, TenantConstants.DEFAULT_TENANT_ID);
        RemoteTenantDataSourceVo tenantDataSource = remoteTenantService.queryDataSourceByTenantId(tenantId);
        // 如果是超级租户,默认设置数据源为master
        if (isSuperTenant && Objects.isNull(tenantDataSource)) {
            DynamicDataSourceContextHolder.push(DEFAULT_TENANT_SOURCE);
            return true;
        }
        if (!isSuperTenant && Objects.isNull(tenantDataSource)) {
            throw new ServiceException("租户编号:" + tenantId + "未配置数据源!!!");
        }
        log.info("租户编号:{}配置的数据源信息为:{}", tenantId, JsonUtils.toJsonString(tenantDataSource));
        // 手动切换
        DynamicDataSourceContextHolder.push(tenantDataSource.getName());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清空当前线程的数据源信息
        DynamicDataSourceContextHolder.clear();
    }

}
