package org.dromara.auth.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 滑块验证码操作处理
 *
 * @author zhou shuai
 */
@SaIgnore
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
public class SliderCaptchaController {

    private final ImageCaptchaApplication imageCaptchaApplication;

    /**
     * 生成验证码
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @GetMapping("/slider/captcha/image")
    public R<ImageCaptchaVO> getSliderCaptchaImage() {
        // 1.生成滑块验证码(该数据返回给前端用于展示验证码数据)
        CaptchaResponse<ImageCaptchaVO> captchaResponse = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
        return R.ok(captchaResponse.getCaptcha());
    }

}
