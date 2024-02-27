package org.dromara.demo.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * sentinel限流 演示案例
 *
 * @author shuai.zhou
 * @date 2021-05-30
 */
@RestController
@RequestMapping("/sentinel")
@Slf4j
public class TestSentinelController {

    /**
     * 授权规则测试
     */
    @GetMapping("/authority/rule")
    public R<Void> authorityRuleTest() {
        log.info("authority rule test...");
        return R.ok();
    }

    /**
     * 流控规则测试
     */
    @GetMapping("/flow/rule")
    public R<Void> flowRuleTest() {
        log.info("flow rule test...");
        return R.ok();
    }

    /**
     * 热点参数规则测试:
     * 热点参数限流对默认的SpringMVC资源无效，需要利用@SentinelResource注解标记资源
     *
     * <p>
     * 默认情况下，Sentinel对控制资源的限流处理是直接抛出异常。设置了统一异常处理BlockExceptionHandler也会发现对非控制层的资源没有用，这样对用户交流不友好，我们需要处理一下@SentinelResource资源的异常信息。
     * <p>
     *
     * @SentinelResource资源的异常处理有两种方式: <p>
     * blockHandler：sentinel定义的失败调用或限制调用，若本次访问被限流或服务降级，则调用blockHandler指定的接口
     * <p>
     * fallback：失败调用，若本接口出现未知异常，则调用fallback指定的接口。
     * <p>
     * 当两个都配置时，也就是当出现sentinel定义的异常时，调用blockHandler，出现其它异常时，调用fallback。
     */
    @GetMapping("/param/flow/rule")
    @SentinelResource(value = "hot", blockHandler = "blockHandlerException")
    public R paramFlowRuleTest(@RequestParam(value = "param1", required = false) String param1) {
        log.info("param flow rule param is {}...", param1);
        return R.ok();
    }

    /**
     * 1.指定被流控后执行的方法: 需要和原方法再同一个类中，同时指定的方法必须是public而且返回值和参数要和原方法一致
     * 2.允许在参数列表的最后加入一个参数BlockException，用来接收原方法中发生的异常。
     * 3.如果是blockHandlerClass,方法必须是 public static
     */
    public R blockHandlerException(String param1, BlockException blockException) {
        String blockMsg = "您的访问过于频繁，请稍后重试!";
        if (blockException instanceof ParamFlowException) {
            blockMsg = StrUtil.format("您对热点参数:{}访问过于频繁，请稍后重试!", param1);
        }
        log.info("blockHandlerException param1 is {}", param1);
        return R.fail(HttpStatus.TOO_MANY_REQUESTS.value(), blockMsg);
    }

}
