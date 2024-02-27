package org.dromara.gateway.config;

import cn.hutool.json.JSONUtil;
import com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * 网关限流配置 (SentinelSCGAutoConfiguration配置类)
 *
 * @author shuai.zhou
 */
@Slf4j
@AutoConfiguration(after = {SentinelSCGAutoConfiguration.class})
public class GatewayConfig {

    /**
     * 自定义限流异常
     *
     * @author: zhou shuai
     * @date: 2024/2/27 0:09
     */
    @PostConstruct
    public void initBlockHandlers() {
        BlockRequestHandler blockRequestHandler = (serverWebExchange, throwable) -> {
            String msg = "非法访问,请稍后重试";
            HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;
            if (throwable instanceof FlowException || throwable instanceof ParamFlowException) {
                msg = "您的访问过于频繁,请稍后重试!";
            } else if (throwable instanceof DegradeException) {
                msg = "调用服务响应异常,已进行降级!";
            } else if (throwable instanceof SystemBlockException) {
                msg = "已触碰系统的红线规则,请检查访问参数!";
            } else if (throwable instanceof AuthorityException) {
                httpStatus = HttpStatus.UNAUTHORIZED;
                msg = "授权规则检测不同,请检查访问参数!";
            }
            R blockResp = R.fail(httpStatus.value(), msg);
            log.info("Blocked by Sentinel Response : {}", JSONUtil.toJsonStr(blockResp));
            return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(blockResp));
        };
        GatewayCallbackManager.setBlockHandler(blockRequestHandler);
    }

}
