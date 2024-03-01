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

package org.dromara.monitor.rocketmq.core;

import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.dromara.monitor.rocketmq.config.MonitorRocketMQProperties;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author shuai.zhou
 */
public interface MonitorListener {
    void beginRound();

    void reportUndoneMsgs(UndoneMsgs undoneMsgs);

    void reportConsumerNotOnline(String consumerGroup);

    void reportFailedMsgs(FailedMsgs failedMsgs);

    void reportDeleteMsgsEvent(DeleteMsgsEvent deleteMsgsEvent);

    void reportConsumerRunningInfo(String consumerGroup, TreeMap<String/* clientId */, ConsumerRunningInfo> criTable, MonitorRocketMQProperties monitorRocketMQProperties);

    void reportStopedBroker(List<String> brokerNames);

    void reportRiskedBroker(Map<String, Map<String, String>> notifyTable);

    void endRound();
}
