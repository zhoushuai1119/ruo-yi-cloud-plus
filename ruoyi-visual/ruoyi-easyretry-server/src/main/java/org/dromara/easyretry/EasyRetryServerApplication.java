package org.dromara.easyretry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.TimeZone;

/**
 * EasyRetry Server 启动程序
 *
 * @author shuai.zhou
 */
@SpringBootApplication(scanBasePackages = {"com.aizuda.easy.retry.server.starter.*"})
@EnableTransactionManagement(proxyTargetClass = true)
public class EasyRetryServerApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(com.aizuda.easy.retry.server.EasyRetryServerApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  easy-retry启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
