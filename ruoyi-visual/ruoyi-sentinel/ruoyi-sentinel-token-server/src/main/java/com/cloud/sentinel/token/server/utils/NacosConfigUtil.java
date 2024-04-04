package com.cloud.sentinel.token.server.utils;

/**
 * Nacos 工具类
 *
 * @author shuai.zhou
 */
public final class NacosConfigUtil {

    /**
     * token-server集群配置 dataId
     */
    private static final String TOKEN_SERVER_CLUSTER_DATA_ID = "token-server-cluster-map.json";
    /**
     * token-server命名空间集合 dataId
     */
    private static final String TOKEN_SERVER_NAMESPACE_SET_DATA_ID = "token-server-namespace-set.json";
    /**
     * 流控规则id
     */
    public static final String FLOW_DATA_ID_POSTFIX = "flow-rules";
    /**
     * 热点规则id
     */
    public static final String PARAM_FLOW_DATA_ID_POSTFIX = "param-flow-rules";
    /**
     * 配置文件内容类型
     */
    public static final String CONFIG_CONTENT_TYPE = "json";

    public static String getTokenServerClusterDataId() {
        return TOKEN_SERVER_CLUSTER_DATA_ID;
    }

    public static String getTokenServerNamespaceSetDataId() {
        return TOKEN_SERVER_NAMESPACE_SET_DATA_ID;
    }

    public static String getConfigContentType() {
        return CONFIG_CONTENT_TYPE;
    }

    public static String getFlowDataId(String appName) {
        return String.format("%s_%s", appName, FLOW_DATA_ID_POSTFIX);
    }


    public static String getParamFlowDataId(String appName) {
        return String.format("%s_%s", appName, PARAM_FLOW_DATA_ID_POSTFIX);
    }

}
