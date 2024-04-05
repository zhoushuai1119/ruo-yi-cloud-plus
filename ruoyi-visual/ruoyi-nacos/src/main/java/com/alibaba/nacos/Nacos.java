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

package com.alibaba.nacos;

import com.alibaba.nacos.annotation.NacosSpringBootApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Nacos starter.
 * <p>
 * Use @SpringBootApplication and @ComponentScan at the same time, using CUSTOM type filter to control module enabled.
 * </p>
 *
 * @author nacos
 */
@NacosSpringBootApplication
@ServletComponentScan
@EnableScheduling
public class Nacos {

    public static void main(String[] args) {
        // true 单机模式 false 为集群模式 集群模式需搭配 cluster.conf 使用 使用方法请查看文档
        System.setProperty("nacos.standalone", "true");
        System.setProperty("server.tomcat.accesslog.enabled", "false");
        SpringApplication.run(Nacos.class, args);
    }
}

