package com.cloud.sentinel.token.server.init;

import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.sentinel.token.server.parser.ClusterServerFlowConfigParser;
import com.cloud.sentinel.token.server.parser.TokenServerTransportConfigParser;
import com.cloud.sentinel.token.server.utils.ApolloConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

/**
 * @description:
 * @author: shuai.zhou
 * @date: 2022/10/24 15:50
 * @version: v1
 * <p>
 * 启动类加上启动参数:
 * -Dcsp.sentinel.log.use.pid=true
 * -Dproject.name=token-server
 * -Dcsp.sentinel.dashboard.server=127.0.0.1:9999
 */
@Slf4j
public class ClusterServerInitFunc implements InitFunc {

    /**
     * sentinel限流规则配置namespace
     */
    private final String sentinelRulesNameSpace = ApolloConfigUtil.getSentinelRulesNamespace();
    /**
     * token server namespace
     */
    private final String tokenServerNameSpace = ApolloConfigUtil.getTokenServeNamespace();
    /**
     * defaultRules
     */
    private final String defaultRules = "[]";

    @Override
    public void init() {

        // 监听特定namespace下的集群限流规则
        initPropertySupplier();

        // 设置tokenServer管辖的作用域(即管理哪些应用)
        initTokenServerNameSpaces();

        // 设置token-server使用哪个端口与token-client通信
        initServerTransportConfig();

        // 初始化最大qps
        initServerFlowConfig();

        //设置为 token server
        initStateProperty();
    }

    /**
     * 监听特定namespace下的集群限流规则
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:30
     */
    private void initPropertySupplier() {
        // Register cluster flow rule property supplier which creates data source by namespace.
        ClusterFlowRuleManager.setPropertySupplier(appName -> {
            ReadableDataSource<String, List<FlowRule>> ds = new ApolloDataSource<>(sentinelRulesNameSpace,
                ApolloConfigUtil.getFlowDataId(appName), defaultRules, source -> JSON.parseObject(source, new TypeReference<>() {
            }));
            return ds.getProperty();
        });

        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(appName -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new ApolloDataSource<>(sentinelRulesNameSpace,
                ApolloConfigUtil.getParamFlowDataId(appName), defaultRules, source -> JSON.parseObject(source, new TypeReference<>() {
            }));
            return ds.getProperty();
        });
    }

    /**
     * 设置tokenServer管辖的作用域(即管理哪些应用)
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:31
     */
    private void initTokenServerNameSpaces() {
        ReadableDataSource<String, Set<String>> namespaceDs = new ApolloDataSource<>(tokenServerNameSpace,
            ApolloConfigUtil.getTokenServerNamespaceSetKey(), defaultRules, source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        ClusterServerConfigManager.registerNamespaceSetProperty(namespaceDs.getProperty());
    }

    /**
     * 设置token-server使用哪个端口与token-client通信
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:32
     */
    private void initServerTransportConfig() {
        ReadableDataSource<String, ServerTransportConfig> serverTransportDs = new ApolloDataSource<>(tokenServerNameSpace,
            ApolloConfigUtil.getTokenServerRuleKey(), defaultRules, new TokenServerTransportConfigParser());
        ClusterServerConfigManager.registerServerTransportProperty(serverTransportDs.getProperty());
    }

    /**
     * 初始化最大qps
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:32
     */
    private void initServerFlowConfig() {
        ReadableDataSource<String, ServerFlowConfig> serverFlowConfig = new ApolloDataSource<>(tokenServerNameSpace,
            ApolloConfigUtil.getTokenServerRuleKey(), defaultRules, new ClusterServerFlowConfigParser());
        ClusterServerConfigManager.registerGlobalServerFlowProperty(serverFlowConfig.getProperty());
    }

    /**
     * 设置为 token server
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:32
     */
    private void initStateProperty() {
        ClusterStateManager.applyState(ClusterStateManager.CLUSTER_SERVER);
    }

}
