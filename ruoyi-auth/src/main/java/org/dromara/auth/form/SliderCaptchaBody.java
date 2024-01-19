package org.dromara.auth.form;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author: zhou shuai
 * @date: 2024/1/18 23:08
 * @version: v1
 */
@Data
public class SliderCaptchaBody implements Serializable {

    @Serial
    private static final long serialVersionUID = -4785106952072373509L;

    private String  id;

    private ImageCaptchaTrack data;

}
