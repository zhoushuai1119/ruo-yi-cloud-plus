package org.dromara.monitor.rocketmq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务类型
 *
 * @author shuai.zhou
 */
@Getter
@AllArgsConstructor
public enum JobTypeEnum {

    REPORT_UNDONE_MSGS("reportUndoneMsgs", "未消费消息"),
    REPORT_CONSUMER_NOT_ONLINE("reportConsumerNotOnline", "消费组不在线"),
    REPORT_FAILED_MSGS("reportFailedMsgs", "消费失败"),
    REPORT_DELETE_MSGS_EVENT("reportDeleteMsgsEvent", "消费消息已被删除或请求消息不存在"),
    SUBSCRIPTION_DIFFERENT("reportConsumerSubscriptionDifferent", "消费者订阅信息不一致"),
    CONSUMER_GROUP_BLOCK("consumerGroupBlock", "消费者阻塞告警"),
    REPORT_STOPED_BROKER("reportStopedBroker", "broker未启动"),
    REPORT_RISKED_BROKER("reportRiskedBroker", "broker运行状态cpu、磁盘、TPS等"),
    MONITOR_WORK_EXCEPTION("MonitorWorkException", "mqm运行异常");

    private final String code;

    private final String codeDesc;

}
