package org.dromara.common.core.exception.user;

import java.io.Serial;

/**
 * 验证码失效异常类
 *
 * @author shuai.zhou
 */
public class CaptchaExpireException extends UserException {

    @Serial
    private static final long serialVersionUID = 1L;

    public CaptchaExpireException() {
        super("user.jcaptcha.expire");
    }
}
