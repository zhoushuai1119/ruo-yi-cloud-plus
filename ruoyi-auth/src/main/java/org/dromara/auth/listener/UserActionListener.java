package org.dromara.auth.listener;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.listener.SaTokenListener;
import cn.dev33.satoken.stp.SaLoginModel;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import cn.hutool.json.JSONUtil;
import com.cloud.mq.base.core.CloudMQTemplate;
import com.cloud.mq.base.dto.CloudMQSendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.common.core.constant.CacheConstants;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.utils.MessageUtils;
import org.dromara.common.core.utils.ServletUtils;
import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.core.utils.ip.AddressUtils;
import org.dromara.common.log.event.LogininforEvent;
import org.dromara.common.redis.utils.RedissonUtils;
import org.dromara.common.satoken.utils.LoginHelper;
import org.dromara.system.api.RemoteUserService;
import org.dromara.system.api.domain.SysUserOnline;
import org.dromara.system.api.model.LoginUser;
import org.springframework.stereotype.Component;

import java.time.Duration;

import static org.dromara.common.core.constant.RocketMQConstants.LoginTopic.*;

/**
 * 用户行为 侦听器的实现
 *
 * @author shuai.zhou
 */
@RequiredArgsConstructor
@Component
@Slf4j
public class UserActionListener implements SaTokenListener {

    private final SaTokenConfig tokenConfig;
    private final CloudMQTemplate cloudMQTemplate;

    @DubboReference
    private RemoteUserService remoteUserService;

    /**
     * 每次登录时触发
     */
    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginModel loginModel) {
        UserAgent userAgent = UserAgentUtil.parse(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = ServletUtils.getClientIP();
        String grantType = String.valueOf(loginModel.getExtra(LoginHelper.GRANT_TYPE));
        LoginUser user = LoginHelper.getLoginUser();
        SysUserOnline userOnline = new SysUserOnline();
        userOnline.setIpaddr(ip);
        userOnline.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        userOnline.setBrowser(userAgent.getBrowser().getName());
        userOnline.setOs(userAgent.getOs().getName());
        userOnline.setLoginTime(System.currentTimeMillis());
        userOnline.setTokenId(tokenValue);
        userOnline.setUserName(user.getUsername());
        userOnline.setClientKey(user.getClientKey());
        userOnline.setDeviceType(user.getDeviceType());
        if (ObjectUtil.isNotNull(user.getDeptName())) {
            userOnline.setDeptName(user.getDeptName());
        }
        if (tokenConfig.getTimeout() == -1) {
            RedissonUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, userOnline);
        } else {
            RedissonUtils.setCacheObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue, userOnline, Duration.ofSeconds(tokenConfig.getTimeout()));
        }
        // 记录登录日志
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(user.getTenantId());
        logininforEvent.setUsername(user.getUsername());
        logininforEvent.setStatus(Constants.LOGIN_SUCCESS);
        logininforEvent.setMessage(MessageUtils.message("user.login.success"));
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
        // 更新登录信息
        remoteUserService.recordLoginInfo(user.getUserId(), ServletUtils.getClientIP());
        log.info("user doLogin, useId:{}, token:{}", loginId, tokenValue);
        try {
            String loginKey = StrFormatter.format(RUOYI_LOGIN_KEY, grantType.toLowerCase(), user.getTenantId(), user.getUserType(), user.getUserId());
            CloudMQSendResult emailMqSendResult = cloudMQTemplate.syncSend(RUOYI_LOGIN_TOPIC, RUOYI_LOGIN_TAG, loginKey, user);
            log.info("{}登录发送MQ结果:{}", grantType.toUpperCase(), JSONUtil.toJsonStr(emailMqSendResult));
        } catch (Exception e) {
            log.warn("{}登录发送MQ结果失败:", grantType.toUpperCase(), e);
        }
    }

    /**
     * 每次注销时触发
     */
    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doLogout, useId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被踢下线时触发
     */
    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doLogoutByLoginId, useId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被顶下线时触发
     */
    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        RedissonUtils.deleteObject(CacheConstants.ONLINE_TOKEN_KEY + tokenValue);
        log.info("user doReplaced, useId:{}, token:{}", loginId, tokenValue);
    }

    /**
     * 每次被封禁时触发
     */
    @Override
    public void doDisable(String loginType, Object loginId, String service, int level, long disableTime) {
    }

    /**
     * 每次被解封时触发
     */
    @Override
    public void doUntieDisable(String loginType, Object loginId, String service) {
    }

    /**
     * 每次打开二级认证时触发
     */
    @Override
    public void doOpenSafe(String loginType, String tokenValue, String service, long safeTime) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCloseSafe(String loginType, String tokenValue, String service) {
    }

    /**
     * 每次创建Session时触发
     */
    @Override
    public void doCreateSession(String id) {
    }

    /**
     * 每次注销Session时触发
     */
    @Override
    public void doLogoutSession(String id) {
    }

    /**
     * 每次Token续期时触发
     */
    @Override
    public void doRenewTimeout(String tokenValue, Object loginId, long timeout) {
    }

}
