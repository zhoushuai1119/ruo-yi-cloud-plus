package com.alibaba.cloud.sentinel.custom.utils;

/**
 * @program: sentinel-parent
 * @description:
 * @author: 01398395
 * @create: 2020-07-21 10:20
 **/
public final class ApolloConfigUtil {

    /**
     * 流控规则id
     */
    public static final String FLOW_DATA_ID_POSTFIX = "flow-rules";

    /**
     * 降级规则id
     */
    public static final String DEGRADE_DATA_ID_POSTFIX = "degrade-rules";

    /**
     * 热点规则id
     */
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "param-flow-rules";

    /**
     * 系统规则id
     */
    public static final String SYSTEM_DATA_ID_POSTFIX = "system-rules";

    /**
     * 授权规则id
     */
    public static final String AUTHORITY_DATA_ID_POSTFIX = "authority-rules";

    /**
     * sentinel限流规则配置namespace
     */
    private static final String SENTINEL_RULES_NAMESPACE = "sentinel-rules";

    /**
     * token server namespace
     */
    private static final String TOKEN_SERVER_NAMESPACE = "token-server";

    /**
     * token server rule key
     */
    private static final String TOKEN_SERVER_RULE_KEY = "token-server-cluster-map";

    /**
     * 客户端请求超时配置
     */
    private static final String CLUSTER_CLIENT_CONFIG = "cluster-client-config";


    public static String getFlowDataId(String appName) {
        return String.format("%s_%s", appName, FLOW_DATA_ID_POSTFIX);
    }

    public static String getDegradeDataId(String appName) {
        return String.format("%s_%s", appName, DEGRADE_DATA_ID_POSTFIX);
    }

    public static String getParamFlowDataId(String appName) {
        return String.format("%s_%s", appName, PARAM_FLOW_DATA_ID_POSTFIX);
    }

    public static String getSystemDataId(String appName) {
        return String.format("%s_%s", appName, SYSTEM_DATA_ID_POSTFIX);
    }

    public static String getAuthorityDataId(String appName) {
        return String.format("%s_%s", appName, AUTHORITY_DATA_ID_POSTFIX);
    }

    public static String getSentinelRulesNamespace() {
        return SENTINEL_RULES_NAMESPACE;
    }


    public static String getTokenServerRulesNamespace() {
        return TOKEN_SERVER_NAMESPACE;
    }

    public static String getTokenServerRuleKey() {
        return TOKEN_SERVER_RULE_KEY;
    }

    public static String getClusterClientConfig() {
        return CLUSTER_CLIENT_CONFIG;
    }

}
