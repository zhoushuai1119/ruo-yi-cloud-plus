package org.dromara.auth.strategy.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.auth.form.XcxLoginBody;
import org.dromara.auth.strategy.AbstractAuthStrategy;
import org.dromara.common.core.enums.LoginType;
import org.dromara.common.core.utils.ValidatorUtils;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.model.XcxLoginUser;
import org.springframework.stereotype.Service;

import static org.dromara.common.core.enums.LoginType.XCX;

/**
 * 小程序认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("xcxAuthStrategy")
public class XcxAuthStrategy extends AbstractAuthStrategy {

    @DubboReference
    private RemoteUserService remoteUserService;

    @Override
    public LoginVo login(String body, RemoteClientVo client) {
        XcxLoginBody loginBody = JsonUtils.parseObject(body, XcxLoginBody.class);
        ValidatorUtils.validate(loginBody);
        // xcxCode 为 小程序调用 wx.login 授权后获取
        String xcxCode = loginBody.getXcxCode();
        // 多个小程序识别使用
        String appid = loginBody.getAppid();

        // todo 以下自行实现
        // 校验 appid + appsrcret + xcxCode 调用登录凭证校验接口 获取 session_key 与 openid
        String openid = "";
        XcxLoginUser loginUser = remoteUserService.getUserInfoByOpenid(openid);
        LoginVo loginVo = loginClient(client, loginUser, loginBody.getGrantType());
        loginVo.setOpenid(openid);
        return loginVo;
    }

    @Override
    public LoginType loginType() {
        return XCX;
    }

}
