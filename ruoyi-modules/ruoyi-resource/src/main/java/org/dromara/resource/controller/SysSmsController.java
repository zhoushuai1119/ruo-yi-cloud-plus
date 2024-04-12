package org.dromara.resource.controller;


import cn.hutool.core.util.RandomUtil;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.Constants;
import org.dromara.common.core.constant.GlobalConstants;
import org.dromara.common.core.domain.R;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.redis.utils.RedissonUtil;
import org.dromara.common.web.core.BaseController;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

/**
 * 短信功能
 *
 * @author shuai.zhou
 */
@Slf4j
@Validated
@RestController
@RequestMapping("/sms")
public class SysSmsController extends BaseController {

    /**
     * 短信验证码
     *
     * @param phonenumber 用户手机号
     */
    @RateLimiter(key = "#phonenumber", time = 60, count = 1)
    @GetMapping("/code/{phonenumber}")
    public R<Void> smsCaptcha(@NotBlank(message = "{user.phonenumber.not.blank}") @PathVariable("phonenumber") String phonenumber) {
        String key = GlobalConstants.PHONE_CODE_KEY + phonenumber;
        String code = RandomUtil.randomNumbers(4);
        RedissonUtil.setCacheObject(key, code, Duration.ofMinutes(Constants.PHONE_CODE_EXPIRATION));
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        SmsResponse smsResponse = smsBlend.sendMessage(phonenumber, code);
        if (!smsResponse.isSuccess()) {
            log.error("验证码短信发送异常 => {}", smsResponse);
            return R.fail(smsResponse.getData().toString());
        }
        return R.ok();
    }

}
