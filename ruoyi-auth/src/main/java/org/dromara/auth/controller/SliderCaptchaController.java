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
import org.dromara.common.ratelimiter.annotation.RateLimiter;
import org.dromara.common.ratelimiter.enums.LimitType;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

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
     * @date: 2024/1/19 19:44
     * @return: cloud.tianai.captcha.spring.vo.CaptchaResponse<cloud.tianai.captcha.spring.vo.ImageCaptchaVO>
     */
    @RateLimiter(time = 60, count = 10, limitType = LimitType.IP)
    @PostMapping("/image")
    public CaptchaResponse<ImageCaptchaVO> getSliderCaptchaImage() {
        // 1.生成滑块验证码(该数据返回给前端用于展示验证码数据)
        CaptchaResponse<ImageCaptchaVO> captchaResponse = imageCaptchaApplication.generateCaptcha(CaptchaTypeConstant.SLIDER);
        return captchaResponse;
    }

    /**
     * 滑块验证码验证
     *
     * @author: zhou shuai
     * @date: 2024/1/19 19:46
     * @param: sliderCaptchaBody
     * @return: cloud.tianai.captcha.common.response.ApiResponse<?>
     */
    @PostMapping("/check")
    public ApiResponse<?> checkCaptcha(@RequestBody SliderCaptchaBody sliderCaptchaBody) {
        ApiResponse<?> sliderCaptchaMatchResp = imageCaptchaApplication.matching(sliderCaptchaBody.getId(), sliderCaptchaBody.getData());
        if (sliderCaptchaMatchResp.isSuccess()) {
            return ApiResponse.ofSuccess(Collections.singletonMap("id", sliderCaptchaBody.getId()));
        }
        return sliderCaptchaMatchResp;
    }

    /**
     * 滑块验证码二次验证
     *
     * @author: zhou shuai
     * @date: 2024/1/19 19:47
     * @param: id
     * @return: boolean
     */
    @GetMapping("/secondary/check")
    public boolean secondaryCheck(@RequestParam("id") String id) {
        boolean secondaryVerification = false;
        // 如果开启了二次验证
        if (imageCaptchaApplication instanceof SecondaryVerificationApplication) {
            secondaryVerification = ((SecondaryVerificationApplication) imageCaptchaApplication).secondaryVerification(id);
        }
        return secondaryVerification;
    }

}
