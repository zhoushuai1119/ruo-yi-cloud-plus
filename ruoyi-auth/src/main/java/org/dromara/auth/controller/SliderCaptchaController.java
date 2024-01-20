package org.dromara.auth.controller;

import cloud.tianai.captcha.common.constant.CaptchaTypeConstant;
import cloud.tianai.captcha.common.response.ApiResponse;
import cloud.tianai.captcha.spring.application.ImageCaptchaApplication;
import cloud.tianai.captcha.spring.vo.CaptchaResponse;
import cloud.tianai.captcha.spring.vo.ImageCaptchaVO;
import cn.dev33.satoken.annotation.SaIgnore;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.auth.form.SliderCaptchaBody;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.concurrent.ThreadLocalRandom;

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

    private final static String RANDOM = "RANDOM";

    /**
     * 生成滑块验证码
     *
     * @author: zhou shuai
     * @date: 2024/1/20 17:56
     * @param: type
     * @return: cloud.tianai.captcha.spring.vo.CaptchaResponse<cloud.tianai.captcha.spring.vo.ImageCaptchaVO>
     */
    @PostMapping("/image")
    public CaptchaResponse<ImageCaptchaVO> getSliderCaptchaImage(@RequestParam(value = "type", required = false) String type) {
        if (StrUtil.isBlank(type)) {
            type = CaptchaTypeConstant.SLIDER;
        }
        if (StrUtil.equalsIgnoreCase(type, RANDOM)) {
            int i = ThreadLocalRandom.current().nextInt(0, 4);
            if (i == 0) {
                type = CaptchaTypeConstant.SLIDER;
            } else if (i == 1) {
                type = CaptchaTypeConstant.CONCAT;
            } else if (i == 2) {
                type = CaptchaTypeConstant.ROTATE;
            } else {
                type = CaptchaTypeConstant.WORD_IMAGE_CLICK;
            }
        }
        // 1.生成滑块验证码(该数据返回给前端用于展示验证码数据)
        CaptchaResponse<ImageCaptchaVO> captchaResponse = imageCaptchaApplication.generateCaptcha(type);
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

}
