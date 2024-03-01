/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dromara.monitor.rocketmq.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author shuai.zhou
 */
@Data
@ConfigurationProperties(prefix = "monitor.rocketmq")
public class MonitorRocketMQProperties {

    /**
     * namesrvAddr
     */
    private String namesrvAddr;
    /**
     * accessKey
     */
    private String accessKey;
    /**
     * secretKey
     */
    private String secretKey;
    /**
     * roundInterval
     */
    private int roundInterval = 1000 * 60;
    /**
     * brokerNames
     */
    private List<String> brokerNames;
    /**
     * 最大消息积累量，超过告警全局默认
     */
    private int undoneMessageMax;
    /**
     * 最大消息积累时间毫秒数
     */
    private int undoneMessageMaxTimeMs;
    /**
     * 最大tps
     */
    private int tpsThreshold;
    /**
     * 磁盘比例限制
     */
    private double diskRatioThreshold;
    /**
     * 最大blocked消息时间
     */
    private int blockedMessageMaxTimeMs = 1000 * 60;
    /**
     * 最大blocked消息数量
     */
    private int blockedMessageTotal = 6000;
    /**
     * 最大消息积累量，消费者 topic 维度
     */
    private Map<String, Map<String, Integer>> undoneMsgConsumer;

    public boolean hasCustomConsumer(String consumerGroup){
        if (Objects.isNull(undoneMsgConsumer)){
            return false;
        }
        return undoneMsgConsumer.containsKey(consumerGroup);
    }

}
