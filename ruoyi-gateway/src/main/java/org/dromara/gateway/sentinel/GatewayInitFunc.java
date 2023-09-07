package org.dromara.gateway.sentinel;

import com.alibaba.csp.sentinel.adapter.gateway.common.api.ApiDefinition;
import com.alibaba.csp.sentinel.adapter.gateway.common.api.GatewayApiDefinitionManager;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayFlowRule;
import com.alibaba.csp.sentinel.adapter.gateway.common.rule.GatewayRuleManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.dromara.gateway.utils.ApolloConfigUtil;

import java.util.List;
import java.util.Set;

/**
 * @author zhoushuai
 */
@Slf4j
public class GatewayInitFunc implements InitFunc {

    /**
     * sentinel 网关规则配置namespace
     */
    private String sentinelGatewayRulesNameSpace = ApolloConfigUtil.getSentinelGatewayRulesNamespace();
    /**
     * defaultRules
     */
    private String defaultRules = "[]";

    @Override
    public void init() {
        String appName = AppNameUtil.getAppName();
        //网关流控规则配置
        registerGatewayFlowRuleProperty(appName);
        //网关API管理规则配置
        registerGatewayApiProperty(appName);
        //降级规则配置
        registerDegradeRuleProperty(appName);
        //系统规则配置
        registerSystemRuleProperty(appName);
    }


    /**
     * 网关流控规则配置
     *
     * @param appName 应用名称
     */
    private void registerGatewayFlowRuleProperty(String appName) {
        ReadableDataSource<String, Set<GatewayFlowRule>> gatewayFlowRuleDataSource = new ApolloDataSource<>(sentinelGatewayRulesNameSpace,
                ApolloConfigUtil.getGatewayFlowDataId(appName), defaultRules, source -> JSON.parseObject(source,
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
        ReadableDataSource<String, Set<ApiDefinition>> apiDefinitionDataSource = new ApolloDataSource<>(sentinelGatewayRulesNameSpace,
                ApolloConfigUtil.getGatewayApiGroupDataId(appName), defaultRules, new GatewayApiParser());
        GatewayApiDefinitionManager.register2Property(apiDefinitionDataSource.getProperty());
    }

    /**
     * 降级规则配置
     *
     * @param appName 应用名称
     */
    private void registerDegradeRuleProperty(String appName) {
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new ApolloDataSource<>(sentinelGatewayRulesNameSpace,
                ApolloConfigUtil.getDegradeDataId(appName), defaultRules, source -> JSON.parseObject(source,
                new TypeReference<List<DegradeRule>>() {
                }));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
    }

    /**
     * 系统规则配置
     *
     * @param appName 应用名称
     */
    private void registerSystemRuleProperty(String appName) {
        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new ApolloDataSource<>(sentinelGatewayRulesNameSpace,
                ApolloConfigUtil.getSystemDataId(appName), defaultRules, source -> JSON.parseObject(source,
                new TypeReference<List<SystemRule>>() {
                }));
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
    }

}
