package org.dromara.monitor.rocketmq.service;

import org.dromara.monitor.rocketmq.dto.PushAlterDTO;

/**
 * @author: zhou shuai
 * @date: 2024/3/1 23:19
 * @version: v1
 */
public interface IDingTalkService {

    /**
     * RocketMQ 企微告警
     *
     * @author: zhou shuai
     * @date: 2024/3/1 23:21
     * @param: pushAlterDTO
     */
    void rocketmqAlarm(PushAlterDTO pushAlterDTO);

}
