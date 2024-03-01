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

import com.alibaba.nacos.shaded.com.google.common.collect.Maps;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.acl.common.AclClientRPCHook;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.PullStatus;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.MQVersion;
import org.apache.rocketmq.common.MixAll;
import org.apache.rocketmq.common.ThreadFactoryImpl;
import org.apache.rocketmq.common.admin.ConsumeStats;
import org.apache.rocketmq.common.admin.OffsetWrapper;
import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.*;
import org.apache.rocketmq.common.protocol.route.BrokerData;
import org.apache.rocketmq.common.protocol.topic.OffsetMovedEvent;
import org.apache.rocketmq.common.topic.TopicValidator;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.dromara.monitor.rocketmq.admin.DefaultMQAdminExt;
import org.dromara.monitor.rocketmq.config.MonitorRocketMQProperties;
import org.dromara.monitor.rocketmq.dto.BrokerStatusDTO;
import org.dromara.monitor.rocketmq.dto.PushAlterDTO;
import org.dromara.monitor.rocketmq.enums.JobTypeEnum;
import org.dromara.monitor.rocketmq.service.IDingTalkService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * @author shuai.zhou
 */
@Slf4j
@Service
public class MonitorService {
    private final ScheduledExecutorService scheduledExecutorService = Executors
        .newSingleThreadScheduledExecutor(new ThreadFactoryImpl("MonitorService"));
    private final DefaultMQPullConsumer defaultMQPullConsumer = new DefaultMQPullConsumer(
        MixAll.TOOLS_CONSUMER_GROUP);
    private final DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer(
        MixAll.MONITOR_CONSUMER_GROUP);
    private DefaultMQAdminExt defaultMQAdminExt;

    private final MonitorRocketMQProperties monitorRocketMQProperties;
    private final MonitorListener monitorListener;
    private final AclClientRPCHook aclRPCHook;
    private final IDingTalkService dingTalkService;

    public MonitorService(MonitorRocketMQProperties monitorRocketMQProperties, MonitorListener monitorListener,
                          AclClientRPCHook aclRPCHook, IDingTalkService dingTalkService) {
        this.monitorRocketMQProperties = monitorRocketMQProperties;
        this.monitorListener = monitorListener;
        this.aclRPCHook = aclRPCHook;
        this.dingTalkService = dingTalkService;
    }

    private void initMqAdmin(AclClientRPCHook aclRPCHook) {
        this.defaultMQAdminExt = new DefaultMQAdminExt(aclRPCHook);
        this.defaultMQAdminExt.setInstanceName(instanceName());
        this.defaultMQAdminExt.setNamesrvAddr(monitorRocketMQProperties.getNamesrvAddr());

        this.defaultMQPullConsumer.setInstanceName(instanceName());
        this.defaultMQPullConsumer.setNamesrvAddr(monitorRocketMQProperties.getNamesrvAddr());

        this.defaultMQPushConsumer.setInstanceName(instanceName());
        this.defaultMQPushConsumer.setNamesrvAddr(monitorRocketMQProperties.getNamesrvAddr());
        try {
            this.defaultMQPushConsumer.setConsumeThreadMin(1);
            this.defaultMQPushConsumer.setConsumeThreadMax(1);
            this.defaultMQPushConsumer.subscribe(TopicValidator.RMQ_SYS_OFFSET_MOVED_EVENT, "*");
            this.defaultMQPushConsumer.registerMessageListener((MessageListenerConcurrently) (msgs, context) -> {
                try {
                    OffsetMovedEvent ome = OffsetMovedEvent.decode(msgs.get(0).getBody(), OffsetMovedEvent.class);
                    DeleteMsgsEvent deleteMsgsEvent = new DeleteMsgsEvent();
                    deleteMsgsEvent.setOffsetMovedEvent(ome);
                    deleteMsgsEvent.setEventTimestamp(msgs.get(0).getStoreTimestamp());

                    MonitorService.this.monitorListener.reportDeleteMsgsEvent(deleteMsgsEvent);
                } catch (Exception ignored) {
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            });
        } catch (MQClientException ignored) {
        }
    }

    @PostConstruct
    public void postConstruct() throws MQClientException {
        this.initMqAdmin(aclRPCHook);
        this.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            private volatile boolean hasShutdown = false;

            @Override
            public void run() {
                synchronized (this) {
                    if (!this.hasShutdown) {
                        this.hasShutdown = true;
                        shutdown();
                    }
                }
            }
        }, "ShutdownHook"));
    }

    private String instanceName() {
        String name = System.currentTimeMillis() + new Random().nextInt() + this.monitorRocketMQProperties.getNamesrvAddr();
        return "MonitorService_" + name.hashCode();
    }

    public void start() throws MQClientException {
        this.defaultMQPullConsumer.start();
        this.defaultMQAdminExt.start();
        this.defaultMQPushConsumer.start();
        this.startScheduleTask();
        log.info(String.format("mq monitor %s", "start"));
    }

    public void shutdown() {
        this.defaultMQPullConsumer.shutdown();
        this.defaultMQAdminExt.shutdown();
        this.defaultMQPushConsumer.shutdown();

        log.info(String.format("mq monitor %s", "shutdown"));
        try {
            TimeUnit.SECONDS.sleep(10L);
        } catch (Exception ignored) {
        }
    }

    private void startScheduleTask() {
        this.scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    doConsumerMonitorWork();
                } catch (Exception e) {
                    log.error("doConsumerMonitorWork Exception", e);
                    PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                        .alarmType(JobTypeEnum.MONITOR_WORK_EXCEPTION.getCode())
                        .alarmContent(String.format("doConsumerMonitorWork \n%s", e))
                        .build();
                    dingTalkService.rocketmqAlarm(pushAlterDTO);
                }

                try {
                    doBrokerMonitorWork();
                } catch (Exception e) {
                    PushAlterDTO pushAlterDTO = PushAlterDTO.builder()
                        .alarmType(JobTypeEnum.MONITOR_WORK_EXCEPTION.getCode())
                        .alarmContent(String.format("doBrokerMonitorWork \n%s", e))
                        .build();
                    dingTalkService.rocketmqAlarm(pushAlterDTO);
                    log.error("doBrokerMonitorWork", e);
                }
            }
        }, 1000 * 20, this.monitorRocketMQProperties.getRoundInterval(), TimeUnit.MILLISECONDS);
    }

    public void doBrokerMonitorWork() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        ClusterInfo clusterInfo = defaultMQAdminExt.examineBrokerClusterInfo();
        Map<String/*brokerName*/, Map<Long/* brokerId */, Object/* brokerDetail */>> brokerServer = Maps.newHashMap();
        Set<String> runningBrokerNames = new HashSet<>();

        List<BrokerStatusDTO> brokerStatusList = new ArrayList<>();

        for (BrokerData brokerData : clusterInfo.getBrokerAddrTable().values()) {
            Map<Long, Object> brokerMasterSlaveMap = Maps.newHashMap();
            for (Entry<Long/* brokerid */, String/* broker address */> brokerAddr : brokerData.getBrokerAddrs().entrySet()) {
                KVTable kvTable = defaultMQAdminExt.fetchBrokerRuntimeStats(brokerAddr.getValue());
                brokerMasterSlaveMap.put(brokerAddr.getKey(), kvTable.getTable());
                brokerStatusList.add(new BrokerStatusDTO(brokerData.getBrokerName(), brokerAddr.getKey(), kvTable));
            }
            runningBrokerNames.add(brokerData.getBrokerName());
            brokerServer.put(brokerData.getBrokerName(), brokerMasterSlaveMap);
        }

        //监控是否有停止运行的broker
        List<String> stopdBrokers = new ArrayList<>();
        for (String brokerName : monitorRocketMQProperties.getBrokerNames()) {
            if (!runningBrokerNames.contains(brokerName)) {
                stopdBrokers.add(brokerName);
            }
        }
        if (!stopdBrokers.isEmpty()) {
            monitorListener.reportStopedBroker(stopdBrokers);
        }
        //判断是否需要输出
        if (!brokerStatusList.isEmpty()) {
            boolean needNotify = false;
            String TT_DISK = "磁盘";
            String TT_PUTTPS = "生产TPS";
            String TT_GETTPS = "消费TPS";
            Map<String, Map<String, String>> mdTable = new LinkedHashMap<>();
            for (BrokerStatusDTO brokerStatus : brokerStatusList) {
                //过滤salve 节点，#0 表示 Master，>0 表示 Slave
                if (brokerStatus.getBrokerId() > 0) {
                    continue;
                }

                HashMap<String, String> prop = new LinkedHashMap<>();
                mdTable.put(brokerStatus.getBrokerName(), prop);
                if (Double.parseDouble(brokerStatus.getDiskRatio()) > monitorRocketMQProperties.getDiskRatioThreshold()) {
                    needNotify = true;
                    prop.put(TT_DISK, "**" + brokerStatus.getDiskRatio() + ", " + brokerStatus.getDiskInfo() + "**");
                } else {
                    prop.put(TT_DISK, brokerStatus.getDiskRatio() + ", " + brokerStatus.getDiskInfo());
                }
                if (Double.parseDouble(brokerStatus.getPutTps()) > monitorRocketMQProperties.getTpsThreshold()) {
                    needNotify = true;
                    prop.put(TT_PUTTPS, "**" + brokerStatus.getPutTps() + "**");
                } else {
                    prop.put(TT_PUTTPS, brokerStatus.getPutTps());
                }
                prop.put(TT_GETTPS, brokerStatus.getGetTps());
            }
            if (needNotify) {
                monitorListener.reportRiskedBroker(mdTable);
            }
        }
    }

    public void doConsumerMonitorWork() throws RemotingException, MQClientException, InterruptedException {
        long beginTime = System.currentTimeMillis();
        this.monitorListener.beginRound();

        TopicList topicList = defaultMQAdminExt.fetchAllTopicList();
        for (String topic : topicList.getTopicList()) {
            if (topic.startsWith(MixAll.RETRY_GROUP_TOPIC_PREFIX)) {
                String consumerGroup = topic.substring(MixAll.RETRY_GROUP_TOPIC_PREFIX.length());

                try {
                    this.reportUndoneMsgs(consumerGroup);
                } catch (Exception e) {
                    // log.error("reportUndoneMsgs Exception", e);
                }

                try {
                    this.reportConsumerRunningInfo(consumerGroup);
                } catch (Exception e) {
                    // log.error("reportConsumerRunningInfo Exception", e);
                }
            }
        }
        this.monitorListener.endRound();
        long spentTimeMills = System.currentTimeMillis() - beginTime;
        log.info("Execute one round monitor work, spent timemills: {}", spentTimeMills);
    }

    private void reportUndoneMsgs(final String consumerGroup) {
        ConsumeStats cs;
        try {
            cs = defaultMQAdminExt.examineConsumeStats(consumerGroup);
        } catch (Exception e) {
            return;
        }

        try {
            defaultMQAdminExt.examineConsumerConnectionInfo(consumerGroup);
        } catch (Exception e) {
            return;
        }

        if (cs != null) {

            HashMap<String/* Topic */, ConsumeStats> csByTopic = new HashMap<String, ConsumeStats>();
            for (Entry<MessageQueue, OffsetWrapper> next : cs.getOffsetTable().entrySet()) {
                MessageQueue mq = next.getKey();
                OffsetWrapper ow = next.getValue();
                ConsumeStats csTmp = csByTopic.get(mq.getTopic());
                if (null == csTmp) {
                    csTmp = new ConsumeStats();
                    csByTopic.put(mq.getTopic(), csTmp);
                }

                csTmp.getOffsetTable().put(mq, ow);
            }

            for (Entry<String, ConsumeStats> next : csByTopic.entrySet()) {
                UndoneMsgs undoneMsgs = new UndoneMsgs();
                undoneMsgs.setConsumerGroup(consumerGroup);
                undoneMsgs.setTopic(next.getKey());
                this.computeUndoneMsgs(undoneMsgs, next.getValue());
                //积压告警
                if (hasUndone(undoneMsgs)) {
                    monitorListener.reportUndoneMsgs(undoneMsgs);
                }
                this.reportFailedMsgs(consumerGroup, next.getKey());
            }
        }
    }

    //是否积压告警
    private boolean hasUndone(UndoneMsgs undoneMsgs) {
        //是否自定义消费者的 告警
        if (monitorRocketMQProperties.hasCustomConsumer(undoneMsgs.getConsumerGroup())) {
            //是否有个性化积压配置,eg monitor.config.undoneMsgConsumer.c_bi_flink_cabinet_store_heartbeat.TP_IOT_DAG_CAB_HB = 50000
            Map<String, Integer> topicUndoMap = monitorRocketMQProperties.getUndoneMsgConsumer().get(undoneMsgs.getConsumerGroup());

            if (null != topicUndoMap.get(undoneMsgs.getTopic())
                && undoneMsgs.getUndoneMsgsTotal() >= topicUndoMap.get(undoneMsgs.getTopic())) {
                log.info("自定义告警积压：{}={}", undoneMsgs.getTopic(), topicUndoMap.get(undoneMsgs.getTopic()));
                return true;
            }
        }

        //默认积压配置
        return undoneMsgs.getUndoneMsgsTotal() >= monitorRocketMQProperties.getUndoneMessageMax()
            //超过特定毫秒数
            && undoneMsgs.getUndoneMsgsDelayTimeMills() >= monitorRocketMQProperties.getUndoneMessageMaxTimeMs();
    }

    public void reportConsumerRunningInfo(final String consumerGroup) throws InterruptedException,
        MQBrokerException, RemotingException, MQClientException {
        ConsumerConnection cc;
        try {
            cc = defaultMQAdminExt.examineConsumerConnectionInfo(consumerGroup);
        } catch (MQBrokerException e) {
            if (e.getResponseCode() == 206) {
                //CODE: 206  DESC: the consumer group[c_example] not online
                monitorListener.reportConsumerNotOnline(consumerGroup);
            }
            throw e;
        }

        TreeMap<String, ConsumerRunningInfo> infoMap = new TreeMap<>();
        for (Connection c : cc.getConnectionSet()) {
            String clientId = c.getClientId();

            if (c.getVersion() < MQVersion.Version.V3_1_8_SNAPSHOT.ordinal()) {
                continue;
            }

            try {
                ConsumerRunningInfo info = defaultMQAdminExt.getConsumerRunningInfo(consumerGroup, clientId, false);
                infoMap.put(clientId, info);
            } catch (Exception ignored) {
            }
        }

        if (!infoMap.isEmpty()) {
            this.monitorListener.reportConsumerRunningInfo(consumerGroup, infoMap, monitorRocketMQProperties);
        }
    }

    private void computeUndoneMsgs(final UndoneMsgs undoneMsgs, final ConsumeStats consumeStats) {
        long total = 0;
        long singleMax = 0;
        long delayMax = 0;
        for (Entry<MessageQueue, OffsetWrapper> next : consumeStats.getOffsetTable().entrySet()) {
            MessageQueue mq = next.getKey();
            OffsetWrapper ow = next.getValue();
            long diff = ow.getBrokerOffset() - ow.getConsumerOffset();

            if (diff > singleMax) {
                singleMax = diff;
            }

            if (diff > 0) {
                total += diff;
            }

            // Delay
            if (ow.getLastTimestamp() > 0) {
                try {
                    long maxOffset = this.defaultMQPullConsumer.maxOffset(mq);
                    if (maxOffset > 0) {
                        PullResult pull = this.defaultMQPullConsumer.pull(mq, "*", maxOffset - 1, 1);
                        if (Objects.requireNonNull(pull.getPullStatus()) == PullStatus.FOUND) {
                            long delay = pull.getMsgFoundList().get(0).getStoreTimestamp() - ow.getLastTimestamp();
                            if (delay > delayMax) {
                                delayMax = delay;
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
        }

        undoneMsgs.setUndoneMsgsTotal(total);
        undoneMsgs.setUndoneMsgsSingleMQ(singleMax);
        undoneMsgs.setUndoneMsgsDelayTimeMills(delayMax);
    }

    private void reportFailedMsgs(final String consumerGroup, final String topic) {
        FailedMsgs failedMsgs = new FailedMsgs();
        failedMsgs.setConsumerGroup(consumerGroup);
        failedMsgs.setTopic(topic);
        monitorListener.reportFailedMsgs(failedMsgs);
    }

}
