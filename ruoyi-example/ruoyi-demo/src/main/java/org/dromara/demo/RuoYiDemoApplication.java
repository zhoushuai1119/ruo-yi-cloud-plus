package org.dromara.demo;

import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 演示模块
 *
 * @author shuai.zhou
 */
@SpringBootApplication
@EsMapperScan("org.dromara.demo.esmapper")
public class RuoYiDemoApplication {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(RuoYiDemoApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  演示模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
