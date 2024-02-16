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


/**
 * @description: Sentinel自定义规则异常返回
 * @author: shuai.zhou
 * @date: 2021/2/1 15:56
 */
public class SentinelBlockHandler implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, BlockException ex) throws Exception {
        String msg = null;
        int status = 429;
        if (ex instanceof FlowException) {
            msg = "请求被限流了!";
        } else if (ex instanceof DegradeException) {
            msg = "请求被降级了!";
        } else if (ex instanceof ParamFlowException) {
            msg = "热点参数限流!";
        } else if (ex instanceof SystemBlockException) {
            msg = "系统规则限流或降级!";
        } else if (ex instanceof AuthorityException) {
            msg = "授权规则不通过!";
            status = 401;
        }

        // http状态码
        response.setStatus(status);
        response.setContentType("application/json;charset=utf-8");
        // spring mvc自带的json操作工具，叫jackson
        String path = request.getServletPath();
        if (path != null) {
            msg = String.format("接口[%s]%s", path, msg);
        }
        new ObjectMapper().writeValue(response.getWriter(), msg);
    }

}
