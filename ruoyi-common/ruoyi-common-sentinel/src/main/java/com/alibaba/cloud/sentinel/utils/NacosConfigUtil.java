package com.alibaba.cloud.sentinel.utils;

/**
 * Nacos 工具类
 *
 * @author shuai.zhou
 */
public final class NacosConfigUtil {

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
     * token-server集群配置 dataId
     */
    private static final String TOKEN_SERVER_CLUSTER_DATA_ID = "token-server-cluster-map.json";

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

    public static String getTokenServerClusterDataId() {
        return TOKEN_SERVER_CLUSTER_DATA_ID;
    }

}
