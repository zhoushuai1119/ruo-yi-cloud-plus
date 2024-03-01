package org.dromara.monitor.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 监控中心
 *
 * @author shuai.zhou
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RocketMQMonitorApplication {

    public static final String ROCKETMQ_CLIENT_LOG_LOADCONFIG = "rocketmq.client.log.loadconfig";

    public static void main(String[] args) {
        System.setProperty(ROCKETMQ_CLIENT_LOG_LOADCONFIG, "false");
        SpringApplication.run(RuoYiMQMonitorApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  RocketMQ监控中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
