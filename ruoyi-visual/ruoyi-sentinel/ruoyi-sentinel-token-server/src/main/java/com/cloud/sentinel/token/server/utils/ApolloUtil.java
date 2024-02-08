package com.cloud.sentinel.token.server.utils;

/**
 * Apollo 工具类
 *
 * @author shuai.zhou
 */
public final class ApolloUtil {

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
     * token server namespace set
     */
    private static final String TOKEN_SERVER_NAMESPACE_SET_KEY = "token-server-namespace-set";
    /**
     * 流控规则id
     */
    public static final String FLOW_DATA_ID_POSTFIX = "flow-rules";
    /**
     * 热点规则id
     */
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "param-flow-rules";


    public static String getFlowDataId(String appName) {
        return String.format("%s_%s", appName, FLOW_DATA_ID_POSTFIX);
    }


    public static String getParamFlowDataId(String appName) {
        return String.format("%s_%s", appName, PARAM_FLOW_DATA_ID_POSTFIX);
    }

    public static String getSentinelRulesNamespace() {
        return SENTINEL_RULES_NAMESPACE;
    }

    public static String getTokenServeNamespace() {
        return TOKEN_SERVER_NAMESPACE;
    }

    public static String getTokenServerRuleKey() {
        return TOKEN_SERVER_RULE_KEY;
    }


    public static String getTokenServerNamespaceSetKey() {
        return TOKEN_SERVER_NAMESPACE_SET_KEY;
    }

}
