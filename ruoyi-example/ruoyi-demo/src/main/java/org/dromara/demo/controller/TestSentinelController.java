package org.dromara.demo.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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
     * 热点参数规则测试
     */
    @GetMapping("/param/flow/rule/{param}")
    @SentinelResource("hot")
    public R<Void> paramFlowRuleTest(@PathVariable("param") String param) {
        log.info("param flow rule param is {}...", param);
        return R.ok();
    }

}
