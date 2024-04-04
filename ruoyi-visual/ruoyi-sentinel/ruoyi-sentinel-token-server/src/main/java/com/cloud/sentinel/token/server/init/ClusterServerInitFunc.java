package com.cloud.sentinel.token.server.init;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.flow.rule.ClusterParamFlowRuleManager;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.cluster.server.config.ServerFlowConfig;
import com.alibaba.csp.sentinel.cluster.server.config.ServerTransportConfig;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.cloud.sentinel.token.server.config.properties.SentinelNacosProperties;
import com.cloud.sentinel.token.server.parser.ClusterServerFlowConfigParser;
import com.cloud.sentinel.token.server.parser.TokenServerTransportConfigParser;
import com.cloud.sentinel.token.server.utils.NacosConfigUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * @description:
 * @author: shuai.zhou
 * @date: 2022/10/24 15:50
 * @version: v1
 */
@Slf4j
public class ClusterServerInitFunc implements InitFunc {

    private final SentinelNacosProperties sentinelNacosProperties = SpringUtil.getBean(SentinelNacosProperties.class);

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
            ReadableDataSource<String, List<FlowRule>> ds = new NacosDataSource<>(sentinelNacosProperties(),
                sentinelNacosProperties.getGroupId(), NacosConfigUtil.getFlowDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
            }));
            return ds.getProperty();
        });

        // Register cluster parameter flow rule property supplier.
        ClusterParamFlowRuleManager.setPropertySupplier(appName -> {
            ReadableDataSource<String, List<ParamFlowRule>> ds = new NacosDataSource<>(sentinelNacosProperties(),
                sentinelNacosProperties.getGroupId(), NacosConfigUtil.getParamFlowDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
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
        ReadableDataSource<String, Set<String>> namespaceDs = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getTokenServerNamespaceSetKey(), source -> JSON.parseObject(source, new TypeReference<>() {
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
        ReadableDataSource<String, ServerTransportConfig> serverTransportDs = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getTokenServerRuleKey(), new TokenServerTransportConfigParser());
        ClusterServerConfigManager.registerServerTransportProperty(serverTransportDs.getProperty());
    }

    /**
     * 初始化最大qps
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:32
     */
    private void initServerFlowConfig() {
        ReadableDataSource<String, ServerFlowConfig> serverFlowConfig = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getTokenServerRuleKey(), new ClusterServerFlowConfigParser());
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
