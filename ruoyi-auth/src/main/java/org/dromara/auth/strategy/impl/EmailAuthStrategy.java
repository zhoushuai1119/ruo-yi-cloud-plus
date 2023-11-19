package org.dromara.auth.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.EmailLoginBody;
import org.dromara.auth.service.SysLoginService;
import org.dromara.auth.strategy.AbstractAuthStrategy;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.user.CaptchaExpireException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.redis.utils.RedisUtils;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;

import static org.dromara.common.core.enums.LoginType.EMAIL;

/**
 * 邮件认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("emailAuthStrategy")
public class EmailAuthStrategy extends AbstractAuthStrategy {

    private final SysLoginService loginService;
    private final RemoteUserService remoteUserService;

    public EmailAuthStrategy(SysLoginService loginService, RemoteUserService remoteUserService) {
        super(loginService, remoteUserService);
        this.loginService = loginService;
        this.remoteUserService = remoteUserService;
    }

    @Override
    public LoginVo login(String body, RemoteClientVo client) {
        EmailLoginBody loginBody = JsonUtils.parseObject(body, EmailLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String tenantId = loginBody.getTenantId();
        String email = loginBody.getEmail();
        String emailCode = loginBody.getEmailCode();
        String grantType = loginBody.getGrantType();

        // 通过邮箱查找用户
        LoginUser loginUser = remoteUserService.getUserInfoByEmail(email, tenantId);
        loginService.checkLogin(loginType(), tenantId, loginUser.getUsername(), () -> !validateEmailCode(tenantId, email, emailCode));
        return loginClient(client, loginUser, grantType);
    }

    @Override
    public LoginType loginType() {
        return EMAIL;
    }

    /**
     * 校验邮箱验证码
     */
    private boolean validateEmailCode(String tenantId, String email, String emailCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + email);
        if (StringUtils.isBlank(code)) {
            loginService.recordLogininfor(tenantId, email, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(emailCode);
    }

}