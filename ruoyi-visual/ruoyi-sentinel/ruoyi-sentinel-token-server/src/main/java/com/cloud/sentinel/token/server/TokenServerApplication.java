package com.cloud.sentinel.token.server;

import com.alibaba.csp.sentinel.log.LogBase;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * @author shuai.zhou
 *
 * 启动类加上启动参数:
 * -Dcsp.sentinel.log.use.pid=true
 * -Dproject.name=token-server
 * -Dcsp.sentinel.dashboard.server=127.0.0.1:9999
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class TokenServerApplication {

    public static void main(String[] args) {
        // 修改sentinel日志生成目录
        System.setProperty(LogBase.LOG_DIR, "/logs/ruoyi/ruoyi-visual/ruoyi-sentinel/ruoyi-sentinel-token-server/sentinel-record");
        SpringApplication.run(TokenServerApplication.class, args);
    }

}
