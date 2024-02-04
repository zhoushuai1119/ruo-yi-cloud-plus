package org.dromara.resource;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 资源服务
 *
 * @author shuai.zhou
 */
@EnableDubbo
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RuoYiResourceApplication {
    public static void main(String[] args) {
        // 修改sentinel日志生成目录
        System.setProperty("csp.sentinel.log.dir", "/logs/ruoyi/ruoyi-resource/sentinel");
        SpringApplication application = new SpringApplication(RuoYiResourceApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  资源服务模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
