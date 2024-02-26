package org.dromara.gateway.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.dromara.common.core.constant.SentinelConstants.SENTINEL_GATEWAY_ERROR_CODE_HEADER;
import static org.dromara.common.core.constant.SentinelConstants.SENTINEL_GATEWAY_ERROR_MSG_HEADER;

/**
 * @author: zhou shuai
 * @date: 2024/2/26 21:00
 * @version: v1
 */
@Slf4j
@Configuration
public class SentinelConfig {
    public SentinelConfig() {
        GatewayCallbackManager.setBlockHandler((serverWebExchange, ex) -> {
            String msg = "非法访问，请稍后重试";
            int statusCode = 429;
            if (ex instanceof FlowException || ex instanceof ParamFlowException) {
                msg = "您的访问过于频繁，请稍后重试!";
            } else if (ex instanceof DegradeException) {
                msg = "调用服务响应异常,已进行降级!";
            } else if (ex instanceof SystemBlockException) {
                msg = "已触碰系统的红线规则，请检查访问参数!";
            } else if (ex instanceof AuthorityException) {
                statusCode = 401;
                msg = "授权规则检测不同，请检查访问参数!";
            }
            R sentinelResp = R.fail(statusCode, msg);
            log.info("Blocked by Sentinel Response : {}", JSONUtil.toJsonStr(sentinelResp));
            return ServerResponse.status(HttpStatus.OK)
                .header(SENTINEL_GATEWAY_ERROR_CODE_HEADER, String.valueOf(statusCode))
                .header(SENTINEL_GATEWAY_ERROR_MSG_HEADER, msg)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(sentinelResp));
        });
    }
}
