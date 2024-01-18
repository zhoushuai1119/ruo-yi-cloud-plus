package org.dromara.auth.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.plugins.secondary.SecondaryVerificationApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.dev33.satoken.annotation.SaIgnore;
import lombok.RequiredArgsConstructor;
import org.dromara.auth.form.SliderCaptchaBody;
import org.dromara.common.core.domain.R;
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 滑块验证码操作处理
 *
 * @author zhou shuai
 */
@SaIgnore
@RequiredArgsConstructor
@RestController
@RequestMapping("/slider/captcha")
public class SliderCaptchaController {

    private final ImageCaptchaApplication imageCaptchaApplication;

    /**
     * 生成滑块验证码
     *
     * @author: zhou shuai
     * @date: 2024/1/18 23:06
     * @return: org.dromara.common.core.domain.R<cloud.tianai.captcha.spring.vo.ImageCaptchaVO>
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @GetMapping("/image")
    public R<ImageCaptchaVO> getSliderCaptchaImage() {
        // 1.生成滑块验证码(该数据返回给前端用于展示验证码数据)
        CaptchaResponse<ImageCaptchaVO> captchaResponse = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
        return R.ok(captchaResponse.getCaptcha());
    }

    /**
     * 滑块验证码验证
     *
     * @author: zhou shuai
     * @date: 2024/1/18 23:14
     * @param: sliderCaptchaBody
     * @return: org.dromara.common.core.domain.R<java.lang.Boolean>
     */
    @PostMapping("/check")
    public R<Boolean> checkCaptcha(@RequestBody @Validated SliderCaptchaBody sliderCaptchaBody) {
        ApiResponse<?> sliderCaptchaMatchResp = imageCaptchaApplication.matching(sliderCaptchaBody.getId(), sliderCaptchaBody.getData());
        return R.ok(sliderCaptchaMatchResp.isSuccess());
    }

    /**
     * 滑块验证码二次验证
     *
     * @author: zhou shuai
     * @date: 2024/1/18 23:06
     * @param: id
     * @return: org.dromara.common.core.domain.R<java.lang.Boolean>
     */
    @GetMapping("/secondary/check")
    public R<Boolean> secondaryCheck(@RequestParam("id") String id) {
        boolean secondaryVerification = false;
        // 如果开启了二次验证
        if (imageCaptchaApplication instanceof SecondaryVerificationApplication) {
            secondaryVerification = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(id);
        }
        return R.ok(secondaryVerification);
    }

}
