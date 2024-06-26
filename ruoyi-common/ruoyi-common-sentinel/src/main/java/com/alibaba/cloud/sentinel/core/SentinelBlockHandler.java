package com.alibaba.cloud.sentinel.core;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.dromara.common.core.domain.R;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;


/**
 * @description: Sentinel自定义规则异常返回
 * @author: shuai.zhou
 * @date: 2021/2/1 15:56
 */
public class SentinelBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws Exception {
        String msg = "非法访问，请稍后重试";
        HttpStatus httpStatus = HttpStatus.TOO_MANY_REQUESTS;
        if (ex instanceof FlowException) {
            msg = "您的访问过于频繁，请稍后重试!";
        } else if (ex instanceof DegradeException) {
            msg = "调用服务响应异常,已进行降级!";
        } else if (ex instanceof ParamFlowException) {
            msg = "您对热点参数访问过于频繁，请稍后重试!";
        } else if (ex instanceof SystemBlockException) {
            msg = "已触碰系统的红线规则，请检查访问参数!";
        } else if (ex instanceof AuthorityException) {
            httpStatus = HttpStatus.UNAUTHORIZED;
            msg = "授权规则检测不同，请检查访问参数!";
        }

        // http状态码
        response.setStatus(httpStatus.value());
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        String errorMsg = String.format("接口[%s]%s", request.getServletPath(), msg);
        new ObjectMapper().writeValue(response.getWriter(), R.fail(httpStatus.value(), errorMsg));
    }

}
