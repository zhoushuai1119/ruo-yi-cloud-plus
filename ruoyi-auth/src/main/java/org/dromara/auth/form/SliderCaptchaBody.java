package org.dromara.auth.form;

import cloud.tianai.captcha.validator.common.model.dto.ImageCaptchaTrack;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "id不能为空")
    private String  id;

    @NotNull(message = "data不能为空")
    private ImageCaptchaTrack data;

}
