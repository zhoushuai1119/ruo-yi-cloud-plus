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

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;
import org.dromara.monitor.rocketmq.config.MonitorRocketMQProperties;
import org.dromara.monitor.rocketmq.dto.PushAlterDTO;
import org.dromara.monitor.rocketmq.enums.JobTypeEnum;
import org.dromara.monitor.rocketmq.utils.MarkdownCreaterUtil;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.Map.Entry;

/**
 * @author shuai.zhou
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DefaultMonitorListener implements MonitorListener {
    private final static String LOG_PREFIX = "[MONITOR] ";
    private final static String LOG_NOTIFY = LOG_PREFIX + " [NOTIFY] ";

    private final DingerSender dingerSender;

    @Override
    public void beginRound() {
        log.info(LOG_PREFIX + "=========================================beginRound");
    }

    @Override
    public void reportUndoneMsgs(UndoneMsgs undoneMsgs) {
        String alarmContent = StrUtil.format(LOG_PREFIX + "reportUndoneMsgs: {}", undoneMsgs);
        log.info(alarmContent);
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_UNDONE_MSGS.getCode())
            .alarmContent(alarmContent)
            .consumerGroup(undoneMsgs.getConsumerGroup())
            .extendedField(undoneMsgs.getTopic())
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void reportConsumerNotOnline(String consumerGroup) {
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_CONSUMER_NOT_ONLINE.getCode())
            .alarmContent(String.format("消费组不在线: %s", consumerGroup))
            .consumerGroup(consumerGroup)
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void reportFailedMsgs(FailedMsgs failedMsgs) {
        String alarmContent = StrUtil.format(LOG_PREFIX + "reportFailedMsgs: {}", failedMsgs);
        log.info(alarmContent);
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_FAILED_MSGS.getCode())
            .alarmContent(alarmContent)
            .consumerGroup(failedMsgs.getConsumerGroup())
            .extendedField(failedMsgs.getTopic())
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void reportDeleteMsgsEvent(DeleteMsgsEvent deleteMsgsEvent) {
        String alarmContent = StrUtil.format(LOG_PREFIX + "reportDeleteMsgsEvent: {}", deleteMsgsEvent);
        log.info(alarmContent);
        String consumerGroup = Optional.ofNullable(deleteMsgsEvent.getOffsetMovedEvent().getConsumerGroup())
            .orElse(deleteMsgsEvent.toString());
        String extendedField = Optional.ofNullable(deleteMsgsEvent.getOffsetMovedEvent().getMessageQueue().toString())
            .orElse(deleteMsgsEvent.toString());

        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_DELETE_MSGS_EVENT.getCode())
            .alarmContent(alarmContent)
            .consumerGroup(consumerGroup)
            .extendedField(extendedField)
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void reportConsumerRunningInfo(TreeMap<String, ConsumerRunningInfo> criTable, MonitorRocketMQProperties monitorRocketMQProperties) {

        {
            boolean result = ConsumerRunningInfo.analyzeSubscription(criTable);
            if (!result) {
                Map<String, Map<String, String>> details = new LinkedHashMap<>();

                Entry<String, ConsumerRunningInfo> prev = criTable.firstEntry();
                for (Entry<String, ConsumerRunningInfo> next : criTable.entrySet()) {
                    boolean equals = next.getValue().getSubscriptionSet().equals(prev.getValue().getSubscriptionSet());
                    if (!equals) {
                        details.put(prev.getKey(), getSubsciptionInfo(prev.getValue().getSubscriptionSet()));
                        details.put(next.getKey(), getSubsciptionInfo(next.getValue().getSubscriptionSet()));
                        break;
                    }
                }

                String consumerGroup = criTable.firstEntry().getValue().getProperties().getProperty("consumerGroup");
                log.info(String.format(LOG_NOTIFY + "reportConsumerRunningInfo: ConsumerGroup: %s, Subscription different \n%s",
                    consumerGroup, MarkdownCreaterUtil.listMarkdown(details)));

                PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                    .alarmType(JobTypeEnum.SUBSCRIPTION_DIFFERENT.getCode())
                    .alarmContent(String.format(LOG_NOTIFY
                            + "同一消费组订阅信息不一致告警: ConsumerGroup: %s, Subscription different \n%s",
                        consumerGroup, MarkdownCreaterUtil.listMarkdown(details)))
                    .build();
                dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
            }
        }

        {
            for (Entry<String, ConsumerRunningInfo> next : criTable.entrySet()) {
                String result = CustomConsumerRunningInfo.analyzeProcessQueue(next.getKey(), next.getValue(), monitorRocketMQProperties);
                if (!result.isEmpty()) {
                    String consumerGroup = criTable.firstEntry().getValue().getProperties().getProperty("consumerGroup");
                    String clientId = next.getKey();
                    log.info(String.format(LOG_NOTIFY
                            + "reportConsumerRunningInfo: ConsumerGroup: %s, ClientId: %s, %s",
                        consumerGroup, clientId, result));

                    PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                        .alarmType(JobTypeEnum.CONSUMER_GROUP_BLOCK.getCode())
                        .alarmContent(String.format(LOG_NOTIFY + "消费者阻塞告警: ConsumerGroup: %s, ClientId: %s, %s",
                            consumerGroup, clientId, result))
                        .consumerGroup(consumerGroup)
                        .extendedField(clientId)
                        .build();
                    dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
                }
            }
        }
    }

    @Override
    public void reportStopedBroker(List<String> brokerNames) {
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_STOPED_BROKER.getCode())
            .alarmContent(String.format(LOG_PREFIX + "broker未启动: %s", brokerNames))
            .extendedField(brokerNames.toString())
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void reportRiskedBroker(Map<String, Map<String, String>> notifyTable) {
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_RISKED_BROKER.getCode())
            .alarmContent(String.format(LOG_PREFIX + "broker运行状态: \n%s", MarkdownCreaterUtil.listMarkdown(notifyTable)))
            .build();
        dingerSender.send(DingerRequest.request(JSONUtil.toJsonStr(pushAlterDTO)));
    }

    @Override
    public void endRound() {
        log.info(LOG_PREFIX + "=========================================endRound");
    }

    private Map<String, String> getSubsciptionInfo(TreeSet<SubscriptionData> subscriptionData) {
        Map<String, String> map = new HashMap<>();
        for (SubscriptionData d : subscriptionData) {
            map.put(d.getTopic(), d.getTagsSet().toString());
        }
        return map;
    }

}
