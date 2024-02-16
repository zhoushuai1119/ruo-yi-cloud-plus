package org.dromara.gateway.utils;

/**
 * @program: sentinel-parent
 * @description:
 * @author shuai.zhou
 * @create: 2020-07-21 10:20
 **/
public final class ApolloConfigUtil {

    /**
     * sentinel网关规则配置namespace
     */
    private static final String SENTINEL_RULES_NAMESPACE = "sentinel-rules";
    /**
     * 网关-api分组id
     */
    public static final String GATEWAY_API_GROUP_DATA_ID_POSTFIX = "gw-api-group-rules";
    /**
     * 网关-流控规则id
     */
    public static final String GATEWAY_FLOW_DATA_ID_POSTFIX = "gw-flow-rules";

    public static String getGatewayFlowDataId(String appName) {
        return String.format("%s_%s", appName, GATEWAY_FLOW_DATA_ID_POSTFIX);
    }

    public static String getGatewayApiGroupDataId(String appName) {
        return String.format("%s_%s", appName, GATEWAY_API_GROUP_DATA_ID_POSTFIX);
    }

    public static String getSentinelRulesNamespace() {
        return SENTINEL_RULES_NAMESPACE;
    }

}
