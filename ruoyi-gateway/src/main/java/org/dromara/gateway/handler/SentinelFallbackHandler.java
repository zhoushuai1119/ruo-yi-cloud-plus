package org.dromara.gateway.handler;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import org.dromara.gateway.utils.WebFluxUtils;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import static org.dromara.common.core.constant.SentinelConstants.SENTINEL_GATEWAY_ERROR_CODE_HEADER;
import static org.dromara.common.core.constant.SentinelConstants.SENTINEL_GATEWAY_ERROR_MSG_HEADER;

/**
 * 自定义限流异常处理
 *
 * @author shuai.zhou
 */
public class SentinelFallbackHandler implements WebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        if (exchange.getResponse().isCommitted()) {
            return Mono.error(ex);
        }
        if (!BlockException.isBlockException(ex)) {
            return Mono.error(ex);
        }
        return handleBlockedRequest(exchange, ex).flatMap(response -> writeResponse(response, exchange));
    }

    private Mono<ServerResponse> handleBlockedRequest(ServerWebExchange exchange, Throwable throwable) {
        return GatewayCallbackManager.getBlockHandler().handleRequest(exchange, throwable);
    }

    private Mono<Void> writeResponse(ServerResponse response, ServerWebExchange exchange) {
        int errorCode = Integer.parseInt(response.headers().get(SENTINEL_GATEWAY_ERROR_CODE_HEADER).get(0));
        String errorMsg = response.headers().get(SENTINEL_GATEWAY_ERROR_MSG_HEADER).get(0);
        return WebFluxUtils.webFluxResponseWriter(exchange.getResponse(), errorMsg, errorCode);
    }

}
