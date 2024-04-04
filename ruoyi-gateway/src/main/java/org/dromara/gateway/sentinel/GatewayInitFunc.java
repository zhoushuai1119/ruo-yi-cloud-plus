package org.dromara.gateway.sentinel;

import com.alibaba.cloud.sentinel.config.SentinelNacosProperties;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.gateway.utils.NacosConfigUtil;

import java.util.Properties;
import java.util.Set;

/**
 * @author zhoushuai
 */
@Slf4j
public class GatewayInitFunc implements InitFunc {

    private final SentinelNacosProperties sentinelNacosProperties = SpringUtils.getBean(SentinelNacosProperties.class);

    @Override
    public void init() {
        String appName = AppNameUtil.getAppName();
        //网关流控规则配置
        registerGatewayFlowRuleProperty(appName);
        //网关API管理规则配置
        registerGatewayApiProperty(appName);
    }


    /**
     * 网关流控规则配置
     *
     * @param appName 应用名称
     */
    private void registerGatewayFlowRuleProperty(String appName) {
        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getGatewayFlowDataId(appName), source -> JSON.parseObject(source,
            new TypeReference<Set<GatewayFlowRule>>() {
            }));
        GatewayRuleManager.register2Property(gatewayFlowRuleDataSource.getProperty());
    }


    /**
     * 网关API管理规则配置
     *
     * @param appName 应用名称
     */
    private void registerGatewayApiProperty(String appName) {
        ReadableDataSource<String, Set<ApiDefinition>> apiDefinitionDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getGatewayApiGroupDataId(appName), new GatewayApiParser());
        GatewayApiDefinitionManager.register2Property(apiDefinitionDataSource.getProperty());
    }

    private Properties sentinelNacosProperties() {
        Properties properties = new Properties();
        // 这里在创建ConfigService实例时加了Nacos实例地址和命名空间两个属性。
        properties.put(PropertyKeyConst.SERVER_ADDR, sentinelNacosProperties.getServerAddr());
        properties.put(PropertyKeyConst.NAMESPACE, sentinelNacosProperties.getNamespace());
        properties.put(PropertyKeyConst.USERNAME, sentinelNacosProperties.getUsername());
        properties.put(PropertyKeyConst.PASSWORD, sentinelNacosProperties.getPassword());
        return properties;
    }

}
