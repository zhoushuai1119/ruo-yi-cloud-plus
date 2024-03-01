package org.dromara.monitor.rocketmq.core;

import org.apache.rocketmq.common.message.MessageQueue;
import org.apache.rocketmq.common.protocol.body.ConsumerRunningInfo;
import org.apache.rocketmq.common.protocol.body.ProcessQueueInfo;
import org.apache.rocketmq.common.protocol.heartbeat.ConsumeType;
import org.dromara.monitor.rocketmq.config.MonitorRocketMQProperties;

import java.util.Map.Entry;

/**
 * @author: zhou shuai
 * @date: 2024/3/1 22:05
 * @version: v1
 */
public class CustomConsumerRunningInfo extends ConsumerRunningInfo {

    public static String analyzeProcessQueue(final String clientId, ConsumerRunningInfo info, MonitorRocketMQProperties monitorRocketMQProperties) {
        StringBuilder sb = new StringBuilder();
        boolean push = false;
        {
            String property = info.getProperties().getProperty(ConsumerRunningInfo.PROP_CONSUME_TYPE);

            if (property == null) {
                property = ((ConsumeType) info.getProperties().get(ConsumerRunningInfo.PROP_CONSUME_TYPE)).name();
            }
            push = ConsumeType.valueOf(property) == ConsumeType.CONSUME_PASSIVELY;
        }

        boolean orderMsg = false;
        {
            String property = info.getProperties().getProperty(ConsumerRunningInfo.PROP_CONSUME_ORDERLY);
            orderMsg = Boolean.parseBoolean(property);
        }

        if (push) {
            for (Entry<MessageQueue, ProcessQueueInfo> next : info.getMqTable().entrySet()) {
                MessageQueue mq = next.getKey();
                ProcessQueueInfo pq = next.getValue();

                if (orderMsg) {

                    if (!pq.isLocked()) {
                        sb.append(String.format("%s %s can't lock for a while, %dms%n",
                                clientId,
                                mq,
                                System.currentTimeMillis() - pq.getLastLockTimestamp()));
                    } else {
                        if (pq.isDroped() && (pq.getTryUnlockTimes() > 0)) {
                            sb.append(String.format("%s %s unlock %d times, still failed%n",
                                    clientId,
                                    mq,
                                    pq.getTryUnlockTimes()));
                        }
                    }

                } else {
                    long diff = System.currentTimeMillis() - pq.getLastConsumeTimestamp();
                    //最后消费时间 1分钟前，积压数据6000 阻塞告警
                    if (diff > monitorRocketMQProperties.getBlockedMessageMaxTimeMs()
                            && pq.getCachedMsgCount() > monitorRocketMQProperties.getBlockedMessageTotal()) {
                        sb.append(String.format("%s %s can't consume for a while, maybe blocked, %dms%n",
                                clientId,
                                mq,
                                diff));
                    }
                }
            }
        }

        return sb.toString();
    }

}
