package org.dromara.demo.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * jasypt加密的样例
 *
 * @author Zhou Shuai
 */
@RestController
@RequestMapping("/jasypt")
@RequiredArgsConstructor
public class TestJasyptController {

    private final StringEncryptor stringEncryptor;

    @Value("${jasypt.test.value}")
    private String jasyptValue;

    /**
     * jasypt配置文件加解密测试
     */
    @GetMapping("/test")
    public R<String> jasyptTest(@RequestParam("value") String value) {
        // 生产环境建议使用命令行参数设置password: -Djasypt.encryptor.password=374d1f97e22a6cf1aa7e42e3fa90fd06

        //  System.setProperty("jasypt.encryptor.password", "374d1f97e22a6cf1aa7e42e3fa90fd06");
        String encValue = stringEncryptor.encrypt(value);
        return R.ok(encValue);
    }

    /**
     * jasypt解密测试
     */
    @GetMapping("/value")
    public R<String> jasyptValue() {
        return R.ok(jasyptValue);
    }

}
