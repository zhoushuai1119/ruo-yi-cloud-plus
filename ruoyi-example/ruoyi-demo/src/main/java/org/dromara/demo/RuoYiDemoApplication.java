package org.dromara.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 演示模块
 *
 * @author shuai.zhou
 */
@SpringBootApplication
public class RuoYiDemoApplication {
    public static void main(String[] args) {
        // 修改sentinel日志生成目录
        System.setProperty("csp.sentinel.log.dir", "/logs/ruoyi/ruoyi-demo/sentinel");
        SpringApplication application = new SpringApplication(RuoYiDemoApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  演示模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
