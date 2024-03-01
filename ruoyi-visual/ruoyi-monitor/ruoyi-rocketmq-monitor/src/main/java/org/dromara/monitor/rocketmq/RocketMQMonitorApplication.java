package org.dromara.monitor.rocketmq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 监控中心
 *
 * @author shuai.zhou
 */
@SpringBootApplication
public class RocketMQMonitorApplication {

    public static void main(String[] args) {
        System.setProperty("rocketmq.client.log.loadconfig", "false");
        SpringApplication.run(RocketMQMonitorApplication.class, args);
        System.out.println("(♥◠‿◠)ﾉﾞ  RocketMQ监控中心启动成功   ლ(´ڡ`ლ)ﾞ  ");
    }
}
