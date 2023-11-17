package org.dromara.auth.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.SocialLoginBody;
import org.dromara.auth.service.SysLoginService;
import org.dromara.auth.strategy.AbstractAuthStrategy;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.exception.ServiceException;
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
public class SocialAuthStrategy extends AbstractAuthStrategy {

    private final SocialProperties socialProperties;
    private final RemoteSocialService remoteSocialService;
    private final RemoteUserService remoteUserService;

    public SocialAuthStrategy(SysLoginService loginService, RemoteUserService remoteUserService,
                              SocialProperties socialProperties, RemoteSocialService remoteSocialService) {
        super(loginService, remoteUserService);
        this.remoteUserService = remoteUserService;
        this.socialProperties = socialProperties;
        this.remoteSocialService = remoteSocialService;
    }

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
        AuthResponse<AuthUser> response = SocialUtils.loginAuth(
            loginBody.getSource(), loginBody.getSocialCode(),
            loginBody.getSocialState(), socialProperties);
        if (!response.ok()) {
            throw new ServiceException(response.getMsg());
        }
        AuthUser authUserData = response.getData();

        String authId = authUserData.getSource() + authUserData.getUuid();

        RemoteSocialVo socialVo = remoteSocialService.selectByAuthId(authId, loginBody.getTenantId());
        if (Objects.isNull(socialVo)) {
            throw new ServiceException("您在当前租户下还没有绑定该第三方账号，绑定后才可以登录！");
        }
        LoginUser loginUser = remoteUserService.getUserInfo(socialVo.getUserId(), loginBody.getTenantId());
        return loginClient(client, loginUser, loginBody.getGrantType());
    }

    @Override
    public LoginType loginType() {
        return SOCIAL;
    }

}
