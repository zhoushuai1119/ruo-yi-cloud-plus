package tech.powerjob.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * powerjob 启动程序
 *
 * @author shuai.zhou
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "tech.powerjob.server")
public class PowerJobServerApplication {

    public static void main(String[] args) {
        //关闭nacos默认日志、解决控制台【as that given for appender [CONFIG_LOG_FILE] defined earlier.】问题
        System.setProperty("nacos.logging.default.config.enabled", "false");
        SpringApplication.run(tech.powerjob.server.PowerJobServerApplication.class, args);
        log.info("文档地址: https://www.yuque.com/powerjob/guidence/problem");
    }

}
