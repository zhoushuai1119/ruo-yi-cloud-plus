package org.dromara.monitor.rocketmq.admin.api;

import lombok.Data;
import org.dromara.monitor.rocketmq.enums.TrackType;

/**
 * @author shuai.zhou
 */
@Data
public class MessageTrack {

    private String consumerGroup;

    private TrackType trackType;

    private String exceptionDesc;

}
