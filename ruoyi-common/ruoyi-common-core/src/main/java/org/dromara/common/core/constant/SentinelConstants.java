package org.dromara.common.core.constant;

/**
 * Sentinel 常量
 *
 * @author shuai.zhou
 */
public interface SentinelConstants {

    /**
     * sentinel授权规则请求头参数名
     */
    String SENTINEL_ORIGIN_HEADER = "origin";
    /**
     * sentinel gateway错误码请求头参数
     */
    String SENTINEL_GATEWAY_ERROR_CODE_HEADER = "sentinel_gateway_error_code";
    /**
     * sentinel gateway错误信息请求头参数
     */
    String SENTINEL_GATEWAY_ERROR_MSG_HEADER = "sentinel_gateway_error_msg";

}
