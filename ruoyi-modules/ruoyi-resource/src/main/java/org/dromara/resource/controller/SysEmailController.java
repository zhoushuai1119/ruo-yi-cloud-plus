package org.dromara.resource.controller;


import cn.hutool.core.util.RandomUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.mail.config.properties.MailProperties;
import org.dromara.common.mail.utils.MailUtils;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.redis.utils.RedissonUtil;
import org.dromara.common.web.core.BaseController;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 邮件功能
 *
 * @author shuai.zhou
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/email")
public class SysEmailController extends BaseController {

    private final MailProperties mailProperties;

    /**
     * 邮箱验证码
     *
     * @param email 邮箱
     */
    @RateLimiter(key = "#email", time = 60, count = 1)
    @GetMapping("/code/{email}")
    public R<Void> emailCode(@NotBlank(message = "{user.email.not.blank}") @PathVariable("email") String email) {
        if (!mailProperties.getEnabled()) {
            return R.fail("当前系统没有开启邮箱功能！");
        }
        String key = GlobalConstants.EMAIL_CODE_KEY + email;
        String code = RandomUtil.randomNumbers(4);
        RedissonUtil.setCacheObject(key, code, Duration.ofMinutes(Constants.EMAIL_CODE_EXPIRATION));
        try {
            MailUtils.sendText(email, "RuoYi-Cloud-Plus邮箱登录验证码", "您本次验证码为：" + code + "，有效性为" + Constants.EMAIL_CODE_EXPIRATION + "分钟，请尽快填写。");
        } catch (Exception e) {
            log.error("验证码短信发送异常 => {}", e.getMessage());
            return R.fail(e.getMessage());
        }
        return R.ok();
    }

}
