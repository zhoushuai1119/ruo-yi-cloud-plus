package org.dromara.auth.strategy;

import cn.dev33.satoken.stp.SaLoginModel;
import cn.dev33.satoken.stp.StpUtil;
import org.dromara.auth.domain.vo.LoginVo;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.domain.vo.RemoteClientVo;
import org.dromara.system.api.model.LoginUser;

/**
 * @description: 登录策略抽象类
 * @author: zhou shuai
 * @date: 2023/11/17 10:20
 * @version: v1
 */
public abstract class AbstractAuthStrategy implements IAuthStrategy {

    public LoginVo loginClient(RemoteClientVo client, LoginUser loginUser, String grantType) {
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());
        SaLoginModel model = new SaLoginModel();
        model.setDevice(client.getDeviceType());
        // 自定义分配 不同用户体系 不同 token 授权时间
        model.setTimeout(client.getTimeout());
        model.setActiveTimeout(client.getActiveTimeout());
        model.setExtra(LoginHelper.CLIENT_KEY, client.getClientId());
        // 生成token
        LoginHelper.login(loginUser, model, grantType);

        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(StpUtil.getTokenValue());
        loginVo.setExpireIn(StpUtil.getTokenTimeout());
        loginVo.setClientId(client.getClientId());
        return loginVo;
    }

}
