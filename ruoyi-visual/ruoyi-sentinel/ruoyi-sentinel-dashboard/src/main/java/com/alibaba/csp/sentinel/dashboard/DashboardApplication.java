/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard;

import com.alibaba.csp.sentinel.init.InitExecutor;
import com.alibaba.csp.sentinel.log.LogBase;
import org.dromara.easyes.starter.register.EsMapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sentinel dashboard application.
 *
 * @author shuai.zhou
 */
@SpringBootApplication
@EsMapperScan("com.alibaba.csp.sentinel.dashboard.mapper")
public class DashboardApplication {

    public static void main(String[] args) {
        //关闭nacos默认日志、解决控制台【as that given for appender [CONFIG_LOG_FILE] defined earlier.】问题
        System.setProperty("nacos.logging.default.config.enabled", "false");
        // 修改sentinel日志生成目录
        System.setProperty(LogBase.LOG_DIR, "/logs/ruoyi/ruoyi-visual/ruoyi-sentinel/ruoyi-sentinel-dashboard/sentinel-record");
        triggerSentinelInit();
        SpringApplication.run(DashboardApplication.class, args);
    }

    private static void triggerSentinelInit() {
        new Thread(InitExecutor::doInit).start();
    }
}
