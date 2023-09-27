package org.dromara.demo.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.demo.service.ITestSeataService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 测试分布式事务Seata的样例
 *
 * @author Zhou Shuai
 */
@RestController
@RequestMapping("/seata")
@RequiredArgsConstructor
public class TestSeataController {

    private final ITestSeataService testSeataService;

    /**
     * 分布式事务Seata测试
     */
    @GetMapping("/test")
    public void seataTest(@RequestParam("value") String value) {
        testSeataService.seataTest(value);
    }

}
