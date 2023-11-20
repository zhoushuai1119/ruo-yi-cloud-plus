package org.dromara.auth.strategy.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.SocialLoginBody;
import org.dromara.auth.service.SysLoginService;
import org.dromara.auth.strategy.AbstractAuthStrategy;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.common.social.config.properties.SocialProperties;
import org.dromara.common.social.utils.SocialUtils;
import org.dromara.system.api.RemoteSocialService;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.domain.vo.RemoteSocialVo;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static org.dromara.common.core.enums.LoginType.SOCIAL;

/**
 * 第三方授权策略
 *
 * @author thiszhc is 三三
 */
@Slf4j
@Service("socialAuthStrategy")
@RequiredArgsConstructor
public class SocialAuthStrategy extends AbstractAuthStrategy {

    private final SocialProperties socialProperties;
    private final SysLoginService loginService;

    @DubboReference
    private RemoteSocialService remoteSocialService;
    @DubboReference
    private RemoteUserService remoteUserService;

    /**
     * 登录-第三方授权登录
     *
     * @param body   登录信息
     * @param client 客户端信息
     */
    @Override
    public LoginVo login(String body, RemoteClientVo client) {
        SocialLoginBody loginBody = JsonUtils.parseObject(body, SocialLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String tenantId = loginBody.getTenantId();
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(), loginBody.getSocialCode(),
            loginBody.getSocialState(), socialProperties);
        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }
        AuthUser authUserData = response.getData();
        String authId = authUserData.getSource() + authUserData.getUuid();
        RemoteSocialVo socialVo = remoteSocialService.selectByAuthId(authId, tenantId);
        if (Objects.isNull(socialVo)) {
            throw new ServiceException("您在当前租户下还没有绑定该第三方账号，绑定后才可以登录！");
        }
        LoginUser loginUser = remoteUserService.getUserInfo(socialVo.getUserId(), tenantId);
        LoginVo loginVo = loginClient(client, loginUser, loginBody.getGrantType());
        loginService.recordLogininfor(loginUser.getTenantId(), socialVo.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));
        remoteUserService.recordLoginInfo(loginUser.getUserId(), ServletUtils.getClientIP());
        return loginVo;
    }

    @Override
    public LoginType loginType() {
        return SOCIAL;
    }

}
