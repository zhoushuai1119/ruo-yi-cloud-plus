package org.dromara.common.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录类型
 *
 * @author shuai.zhou
 */
@Getter
@AllArgsConstructor
public enum LoginType {

    /**
     * 密码登录
     */
    PASSWORD("password", "user.password.retry.limit.exceed", "user.password.retry.limit.count"),

    /**
     * 短信登录
     */
    SMS("sms", "sms.code.retry.limit.exceed", "sms.code.retry.limit.count"),

    /**
     * 邮箱登录
     */
    EMAIL("email", "email.code.retry.limit.exceed", "email.code.retry.limit.count"),

    /**
     * 第三方登录
     */
    SOCIAL("social", "", ""),

    /**
     * 小程序登录
     */
    XCX("xcx", "", "");

    /**
     * 登录策略编码
     */
    final String code;
    /**
     * 登录重试超出限制提示
     */
    final String retryLimitExceed;

    /**
     * 登录重试限制计数提示
     */
    final String retryLimitCount;
}
