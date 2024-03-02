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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.heartbeat.SubscriptionData;
import org.dromara.monitor.rocketmq.config.MonitorRocketMQProperties;
import org.dromara.monitor.rocketmq.dto.PushAlterDTO;
import org.dromara.monitor.rocketmq.enums.JobTypeEnum;
import org.dromara.monitor.rocketmq.service.IDingTalkService;
import org.dromara.monitor.rocketmq.utils.MarkdownUtil;
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

    private final IDingTalkService dingTalkService;

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
        dingTalkService.rocketmqAlarm(pushAlterDTO);
    }

    @Override
    public void reportConsumerNotOnline(String consumerGroup) {
        if (Objects.equals(consumerGroup, MixAll.TOOLS_CONSUMER_GROUP)) {
            return;
        }
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_CONSUMER_NOT_ONLINE.getCode())
            .alarmContent(StrUtil.format("消费组不在线: {}", consumerGroup))
            .consumerGroup(consumerGroup)
            .build();
        dingTalkService.rocketmqAlarm(pushAlterDTO);
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
        dingTalkService.rocketmqAlarm(pushAlterDTO);
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
        dingTalkService.rocketmqAlarm(pushAlterDTO);
    }

    @Override
    public void reportConsumerRunningInfo(String consumerGroup, TreeMap<String, ConsumerRunningInfo> criTable, MonitorRocketMQProperties monitorRocketMQProperties) {

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

                log.info(StrUtil.format(LOG_NOTIFY + "reportConsumerRunningInfo: ConsumerGroup: {}, Subscription different \n{}",
                    consumerGroup, MarkdownUtil.listMarkdown(details)));

                PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                    .alarmType(JobTypeEnum.SUBSCRIPTION_DIFFERENT.getCode())
                    .alarmContent(StrUtil.format(LOG_NOTIFY
                            + "同一消费组订阅信息不一致告警: ConsumerGroup: {}, Subscription different \n{}",
                        consumerGroup, MarkdownUtil.listMarkdown(details)))
                    .build();
                dingTalkService.rocketmqAlarm(pushAlterDTO);
            }
        }

        {
            for (Entry<String, ConsumerRunningInfo> next : criTable.entrySet()) {
                String result = CustomConsumerRunningInfo.analyzeProcessQueue(next.getKey(), next.getValue(), monitorRocketMQProperties);
                if (StrUtil.isNotBlank(result)) {
                    String clientId = next.getKey();
                    log.info(StrUtil.format(LOG_NOTIFY
                            + "reportConsumerRunningInfo: ConsumerGroup: {}, ClientId: {}, {}",
                        consumerGroup, clientId, result));

                    PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                        .alarmType(JobTypeEnum.CONSUMER_GROUP_BLOCK.getCode())
                        .alarmContent(StrUtil.format(LOG_NOTIFY + "消费者阻塞告警: ConsumerGroup: {}, ClientId: {}, {}",
                            consumerGroup, clientId, result))
                        .consumerGroup(consumerGroup)
                        .extendedField(clientId)
                        .build();
                    dingTalkService.rocketmqAlarm(pushAlterDTO);
                }
            }
        }
    }

    @Override
    public void reportStopedBroker(List<String> brokerNames) {
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_STOPED_BROKER.getCode())
            .alarmContent(StrUtil.format(LOG_PREFIX + "broker未启动: {}", brokerNames))
            .extendedField(brokerNames.toString())
            .build();
        dingTalkService.rocketmqAlarm(pushAlterDTO);
    }

    @Override
    public void reportRiskedBroker(Map<String, Map<String, String>> notifyTable) {
        PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
            .alarmType(JobTypeEnum.REPORT_RISKED_BROKER.getCode())
            .alarmContent(StrUtil.format(LOG_PREFIX + "broker运行状态: \n{}", MarkdownUtil.listMarkdown(notifyTable)))
            .build();
        dingTalkService.rocketmqAlarm(pushAlterDTO);
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
