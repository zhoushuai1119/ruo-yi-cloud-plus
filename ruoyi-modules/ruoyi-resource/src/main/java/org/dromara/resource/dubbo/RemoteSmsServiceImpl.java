package org.dromara.resource.dubbo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.common.core.exception.ServiceException;
import org.dromara.common.json.utils.JsonUtils;
import org.dromara.resource.api.RemoteSmsService;
import org.dromara.resource.api.domain.RemoteSms;
import org.dromara.sms4j.api.SmsBlend;
import org.dromara.sms4j.api.entity.SmsResponse;
import org.dromara.sms4j.core.factory.SmsFactory;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;

/**
 * 短信服务
 *
 * @author Lion Li
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSmsServiceImpl implements RemoteSmsService {

    /**
     * 发送短信
     *
     * @param phones  电话号(多个逗号分割)
     * @param message message
     */
    @Override
    public RemoteSms send(String phones, String message) throws ServiceException {
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        SmsResponse smsResponse = smsBlend.sendMessage(phones, message);
        RemoteSms sysSms = new RemoteSms();
        sysSms.setIsSuccess(smsResponse.isSuccess());
        sysSms.setMessage(String.valueOf(smsResponse.getData()));
        sysSms.setResponse(JsonUtils.toJsonString(smsResponse));
        return sysSms;
    }

    /**
     * 发送短信
     *
     * @param phones     电话号(多个逗号分割)
     * @param templateId 模板id
     * @param param      模板对应参数
     */
    public RemoteSms send(String phones, String templateId, LinkedHashMap<String, String> param) throws ServiceException {
        SmsBlend smsBlend = SmsFactory.getSmsBlend("alibaba");
        SmsResponse smsResponse = smsBlend.sendMessage(phones, templateId, param);
        RemoteSms sysSms = new RemoteSms();
        sysSms.setIsSuccess(smsResponse.isSuccess());
        sysSms.setMessage(String.valueOf(smsResponse.getData()));
        sysSms.setResponse(JsonUtils.toJsonString(smsResponse));
        return sysSms;
    }

}
