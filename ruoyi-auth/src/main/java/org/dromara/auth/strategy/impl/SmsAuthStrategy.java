package org.dromara.auth.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.SmsLoginBody;
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

import static org.dromara.common.core.enums.LoginType.SMS;

/**
 * 短信认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("smsAuthStrategy")
public class SmsAuthStrategy extends AbstractAuthStrategy {

    private final SysLoginService loginService;
    private final RemoteUserService remoteUserService;

    public SmsAuthStrategy(SysLoginService loginService, RemoteUserService remoteUserService) {
        super(loginService, remoteUserService);
        this.loginService = loginService;
        this.remoteUserService = remoteUserService;
    }

    @Override
    public LoginVo login(String body, RemoteClientVo client) {
        SmsLoginBody loginBody = JsonUtils.parseObject(body, SmsLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String tenantId = loginBody.getTenantId();
        String phonenumber = loginBody.getPhonenumber();
        String smsCode = loginBody.getSmsCode();
        String grantType = loginBody.getGrantType();

        // 通过手机号查找用户
        LoginUser loginUser = remoteUserService.getUserInfoByPhonenumber(phonenumber, tenantId);
        loginService.checkLogin(loginType(), tenantId, loginUser.getUsername(), () -> !validateSmsCode(tenantId, phonenumber, smsCode));
        return loginClient(client, loginUser, grantType);
    }

    @Override
    public LoginType loginType() {
        return SMS;
    }

    /**
     * 校验短信验证码
     */
    private boolean validateSmsCode(String tenantId, String phonenumber, String smsCode) {
        String code = RedisUtils.getCacheObject(GlobalConstants.CAPTCHA_CODE_KEY + phonenumber);
        if (StringUtils.isBlank(code)) {
            loginService.recordLogininfor(tenantId, phonenumber, Constants.LOGIN_FAIL, MessageUtils.message("user.jcaptcha.expire"));
            throw new CaptchaExpireException();
        }
        return code.equals(smsCode);
    }

}