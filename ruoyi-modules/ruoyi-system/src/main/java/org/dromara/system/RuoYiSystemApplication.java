package org.dromara.system;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 系统模块
 *
 * @author shuai.zhou
 */
@EnableDubbo
@SpringBootApplication
public class RuoYiSystemApplication {
    public static void main(String[] args) {
        // 修改sentinel日志生成目录
        System.setProperty("csp.sentinel.log.dir", "/logs/ruoyi/ruoyi-system/sentinel");
        SpringApplication application = new SpringApplication(RuoYiSystemApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  系统模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
