package org.dromara.common.core.constant;

/**
 * @author: zhou shuai
 * @date: 2023/11/1 19:42
 * @version: v1
 */
public class RocketMQConstants {

    public interface LoginTopic {
        /**
         * 登录RocketMQ Topic
         */
        String RUOYI_LOGIN_TOPIC = "RUOYI_PLUS_LOGIN_TOPIC";
        /**
         * 登录RocketMQ Tag
         */
        String RUOYI_LOGIN_TAG = "RUOYI_PLUS_LOGIN_TAG";
        /**
         * 登录RocketMQ Key 格式: grantType_login_tenantId_userType_userId
         */
        String RUOYI_LOGIN_KEY = "{}_login_{}_{}_{}";
    }

}
