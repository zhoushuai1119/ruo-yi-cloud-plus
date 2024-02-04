package org.dromara.job;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;

/**
 * 任务调度模块
 *
 * @author shuai.zhou
 */
@EnableDubbo
@SpringBootApplication
public class RuoYiJobApplication {

    public static void main(String[] args) {
        // 修改sentinel日志生成目录
        System.setProperty("csp.sentinel.log.dir", "/logs/ruoyi/ruoyi-job/sentinel");
        SpringApplication application = new SpringApplication(RuoYiJobApplication.class);
        application.setApplicationStartup(new BufferingApplicationStartup(2048));
        application.run(args);
        System.out.println("(♥◠‿◠)ﾉﾞ  任务调度模块启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }

}
