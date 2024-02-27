package com.alibaba.cloud.sentinel.handler;

import cn.hutool.http.HttpStatus;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 自定义全局限流系统异常处理器:
 *
 * <p>
 * 如果是程序中出现的 Sentinel 报错信息，例如使用热点限流时，因为要配合使用 @SentinelResource 注解时，
 * 此时只自定义了 value 属性，未定义局部 blockHandler 方法，此时系统就会报错，
 * 但这个时候并不会执行 Sentinel 全局自定义异常SentinelBlockHandler，而是程序报错，
 * 此时就需要使用系统自定义异常来重新定义异常信息了
 * </p>
 *
 * <p>
 * 执行优先级是：自定义局部异常 > 自定义全局异常 > 自定义系统异常
 * <p>
 * 参考链接: https://www.jb51.net/program/305568xon.htm
 *
 * @author shuai.zhou
 */
@Slf4j
@RestControllerAdvice
public class GlobalBlockExceptionHandler {

    /**
     * 限流全局异常
     */
    @ExceptionHandler(FlowException.class)
    public R<Void> handleFlowException(FlowException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}'访问过于频繁,被限流！", requestURI);
        return R.fail(HttpStatus.HTTP_TOO_MANY_REQUESTS, "您的访问过于频繁，请稍后重试！");
    }

    /**
     * 热点参数限流异常
     */
    @ExceptionHandler(ParamFlowException.class)
    public R<Void> handleParamFlowException(ParamFlowException e, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        log.error("请求地址'{}'对热点参数:{}访问过于频繁,被限流！", requestURI, e.getMessage());
        return R.fail(HttpStatus.HTTP_FORBIDDEN, "您对热点参数访问过于频繁，请稍后重试！");
    }

}
