package org.dromara.system.listener;

import cn.hutool.json.JSONUtil;
import com.cloud.mq.base.dto.CloudMessage;
import com.cloud.platform.rocketmq.annotation.ConsumeTopic;
import com.cloud.platform.rocketmq.core.TopicListener;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.enums.UserType;
import org.dromara.system.api.model.LoginUser;

import java.util.Objects;

import static org.dromara.common.core.constant.RocketMQConstants.LoginTopic.RUOYI_LOGIN_TOPIC;
import static org.dromara.common.core.constant.RocketMQConstants.LoginTopic.RUOYI_LOGIN_TAG;

/**
 * @description: 登录MQ监听
 * @author: zhou shuai
 * @date: 2023/11/1 14:17
 * @version: v1
 */
@Slf4j
@ConsumeTopic(topic = RUOYI_LOGIN_TOPIC, eventCode = RUOYI_LOGIN_TAG, log = true)
public class LoginListener implements TopicListener<LoginUser> {

    @Override
    public void onMessage(CloudMessage<LoginUser> loginUserMsg) {
        String loginKey = loginUserMsg.getKey();
        LoginUser loginUser = loginUserMsg.getPayload();
        String loginId = loginUser.getLoginId();
        String userType = loginUser.getUserType();
        log.info("监听到登录消息:{}; loginKey:{}", JSONUtil.toJsonStr(loginUser), loginKey);
        if (Objects.equals(userType, UserType.APP_USER.getUserType())) {
            // app端 自行根据业务编写
        }
    }

}
