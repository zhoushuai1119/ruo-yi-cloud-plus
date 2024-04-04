package com.alibaba.cloud.sentinel.core;

import com.alibaba.cloud.sentinel.config.SentinelNacosProperties;
import com.alibaba.cloud.sentinel.parser.ClusterAssignConfigParser;
import com.alibaba.cloud.sentinel.utils.NacosConfigUtil;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.nacos.NacosDataSource;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRuleManager;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.config.TransportConfig;
import com.alibaba.csp.sentinel.util.AppNameUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.PropertyKeyConst;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.utils.SpringUtils;

import java.util.List;
import java.util.Properties;

/**
 * @author zhoushuai
 * <p>
 * 本地多应用启动需要添加启动参数: -Dcsp.sentinel.log.use.pid=true
 */
@Slf4j
public class ClusterClientInitFunc implements InitFunc {

    private final SentinelNacosProperties sentinelNacosProperties = SpringUtils.getBean(SentinelNacosProperties.class);

    @Override
    public void init() {
        //初始化普通限流规则
        initClientRules();

        //初始化集群限流规则
        initClusterConfig();
    }

    /**
     * 初始化普通限流规则
     */
    private void initClientRules() {
        String appName = AppNameUtil.getAppName();
        //流控规则
        registerFlowRuleProperty(appName);
        //降级规则
        registerDegradeRuleProperty(appName);
        //热点参数规则
        registerParamFlowRuleProperty(appName);
        //授权规则
        registerAuthorityRuleProperty(appName);
        //系统规则
        registerSystemRuleProperty(appName);
    }

    /**
     * 初始化集群限流规则
     */
    private void initClusterConfig() {
        //等待transport端口分配完毕
        while (TransportConfig.getRuntimePort() == -1) {
            try {
                Thread.sleep(10);
            } catch (Exception ignored) {

            }
        }

        // 集群限流客户端的配置属性
        // initClientConfigProperty();
        //初始化Token客户端
        initClientServerAssignProperty();
        //初始化客户端状态为client
        initStateProperty();
    }


    /**
     * 限流规则配置:
     * 当nacos配置发生变化时，会触发NacosDataSource中的parser调整Property的，
     * 并触发FlowRuleManager中FlowPropertyListener监听的configUpdate方法进行配置调整
     *
     * @param appName 应用名称
     */
    private void registerFlowRuleProperty(String appName) {
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getFlowDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        //获取nacos中的配置写入本地缓存配置中
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }


    /**
     * 降级规则配置
     *
     * @param appName 应用名称
     */
    private void registerDegradeRuleProperty(String appName) {
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getDegradeDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
    }

    /**
     * 热点参数规则配置
     *
     * @param appName 应用名称
     */
    private void registerParamFlowRuleProperty(String appName) {
        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getParamFlowDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());
    }


    /**
     * 授权规则配置
     *
     * @param appName 应用名称
     */
    private void registerAuthorityRuleProperty(String appName) {
        ReadableDataSource<String, List<AuthorityRule>> authorityRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getAuthorityDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        AuthorityRuleManager.register2Property(authorityRuleDataSource.getProperty());
    }

    /**
     * 系统规则配置
     *
     * @param appName 应用名称
     */
    private void registerSystemRuleProperty(String appName) {
        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getSystemDataId(appName), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
    }

    /**
     * 集群限流客户端的配置属性
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:34
     */
    /*private void initClientConfigProperty() {
        ReadableDataSource<String, ClusterClientConfig> clientConfigDs = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getTokenServerRuleKey(), source -> JSON.parseObject(source, new TypeReference<>() {
        }));
        ClusterClientConfigManager.registerClientConfigProperty(clientConfigDs.getProperty());
    }*/

    /**
     * 设置 token client 需要链接的token server 的地址
     *
     * @author: zhou shuai
     * @date: 2024/2/19 21:35
     */
    public void initClientServerAssignProperty() {
        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignDs = new NacosDataSource<>(sentinelNacosProperties(),
            sentinelNacosProperties.getGroupId(), NacosConfigUtil.getTokenServerClusterDataId(), new ClusterAssignConfigParser());
        ClusterClientConfigManager.registerServerAssignProperty(clientAssignDs.getProperty());
    }

    /**
     * 设置业务端状态为集群限流客户端
     */
    private void initStateProperty() {
        ClusterStateManager.applyState(ClusterStateManager.CLUSTER_CLIENT);
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
