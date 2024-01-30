package org.dromara.demo.controller;

import cn.hutool.core.util.RandomUtil;
import lombok.RequiredArgsConstructor;
import org.dromara.common.core.domain.R;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 短信演示案例
 * 请先阅读文档 否则无法使用
 *
 * @author Lion Li
 * @version 4.2.0
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/sms")
public class SmsController {

    /**
     * 发送短信Aliyun
     *
     * @param phones     电话号
     */
    @GetMapping("/sendAliyun")
    public R<Object> sendAliyun(String phones) {
        String code = RandomUtil.randomNumbers(4);
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        SmsResponse smsResponse = smsBlend.sendMessage(phones, code);
        return R.ok(smsResponse);
    }

    /**
     * 添加黑名单
     *
     * @param phone 手机号
     */
    @GetMapping("/addBlacklist")
    public R<Object> addBlacklist(String phone){
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        smsBlend.joinInBlacklist(phone);
        return R.ok();
    }

    /**
     * 移除黑名单
     *
     * @param phone 手机号
     */
    @GetMapping("/removeBlacklist")
    public R<Object> removeBlacklist(String phone){
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        smsBlend.removeFromBlacklist(phone);
        return R.ok();
    }

}
