package org.dromara.common.sentinel.core;

import org.dromara.common.sentinel.utils.ApolloConfigUtil;
import com.alibaba.csp.sentinel.cluster.ClusterStateManager;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientAssignConfig;
import com.alibaba.csp.sentinel.cluster.client.config.ClusterClientConfigManager;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.apollo.ApolloDataSource;
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
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @author zhoushuai
 * <p>
 * 本地多应用启动需要添加启动参数: -Dcsp.sentinel.log.use.pid=true
 */
@Slf4j
public class ClusterClientInitFunc implements InitFunc {
    /**
     * token server namespace
     */
    private final String tokenServerNameSpace = ApolloConfigUtil.getTokenServerRulesNamespace();
    /**
     * sentinel限流规则配置namespace
     */
    private final String sentinelRulesNameSpace = ApolloConfigUtil.getSentinelRulesNamespace();
    /**
     * defaultRules
     */
    private final String defaultRules = "[]";

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
        //为每个client设置目标token server
        initClientServerAssignProperty(tokenServerNameSpace);
        //初始化客户端状态为client 或者 server
        initStateProperty();
    }


    /**
     * 限流规则配置:
     * 当apollo配置发生变化时，会触发ApolloDataSource中的parser调整Property的，
     * 并触发FlowRuleManager中FlowPropertyListener监听的configUpdate方法进行配置调整
     *
     * @param appName 应用名称
     */
    private void registerFlowRuleProperty(String appName) {
        ReadableDataSource<String, List<FlowRule>> flowRuleDataSource = new ApolloDataSource<>(sentinelRulesNameSpace,
            ApolloConfigUtil.getFlowDataId(appName), defaultRules, source -> JSON.parseObject(source,
            new TypeReference<List<FlowRule>>() {
            }));
        //获取apollo中的配置写入本地缓存配置中
        FlowRuleManager.register2Property(flowRuleDataSource.getProperty());
    }


    /**
     * 降级规则配置
     *
     * @param appName 应用名称
     */
    private void registerDegradeRuleProperty(String appName) {
        ReadableDataSource<String, List<DegradeRule>> degradeRuleDataSource = new ApolloDataSource<>(sentinelRulesNameSpace,
            ApolloConfigUtil.getDegradeDataId(appName), defaultRules, source -> JSON.parseObject(source,
            new TypeReference<List<DegradeRule>>() {
            }));
        DegradeRuleManager.register2Property(degradeRuleDataSource.getProperty());
    }

    /**
     * 热点参数规则配置
     *
     * @param appName 应用名称
     */
    private void registerParamFlowRuleProperty(String appName) {
        ReadableDataSource<String, List<ParamFlowRule>> paramFlowRuleDataSource = new ApolloDataSource<>(sentinelRulesNameSpace,
            ApolloConfigUtil.getParamFlowDataId(appName), defaultRules, source -> JSON.parseObject(source,
            new TypeReference<List<ParamFlowRule>>() {
            }));
        ParamFlowRuleManager.register2Property(paramFlowRuleDataSource.getProperty());
    }


    /**
     * 授权规则配置
     *
     * @param appName 应用名称
     */
    private void registerAuthorityRuleProperty(String appName) {
        ReadableDataSource<String, List<AuthorityRule>> authorityRuleDataSource = new ApolloDataSource<>(sentinelRulesNameSpace,
            ApolloConfigUtil.getAuthorityDataId(appName), defaultRules, source -> JSON.parseObject(source,
            new TypeReference<List<AuthorityRule>>() {
            }));
        AuthorityRuleManager.register2Property(authorityRuleDataSource.getProperty());
    }

    /**
     * 系统规则配置
     *
     * @param appName 应用名称
     */
    private void registerSystemRuleProperty(String appName) {
        ReadableDataSource<String, List<SystemRule>> systemRuleDataSource = new ApolloDataSource<>(sentinelRulesNameSpace,
            ApolloConfigUtil.getSystemDataId(appName), defaultRules, source -> JSON.parseObject(source,
            new TypeReference<List<SystemRule>>() {
            }));
        SystemRuleManager.register2Property(systemRuleDataSource.getProperty());
    }

    /**
     * 设置 token client 需要链接的token server 的地址
     */
    public void initClientServerAssignProperty(String tokenServerNameSpace) {
        ReadableDataSource<String, ClusterClientAssignConfig> clientAssignDs = new ApolloDataSource<>(tokenServerNameSpace,
            ApolloConfigUtil.getTokenServerRuleKey(), defaultRules, new ClusterAssignConfigParser());
        ClusterClientConfigManager.registerServerAssignProperty(clientAssignDs.getProperty());
    }

    /**
     * 设置业务端状态为集群限流客户端
     */
    private void initStateProperty() {
        ClusterStateManager.applyState(ClusterStateManager.CLUSTER_CLIENT);
    }

}
