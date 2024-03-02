package org.dromara.monitor.rocketmq.service.impl;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.core.entity.enums.MessageSubType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.dromara.monitor.rocketmq.dto.PushAlterDTO;
import org.dromara.monitor.rocketmq.enums.JobTypeEnum;
import org.dromara.monitor.rocketmq.service.IDingTalkService;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Objects;

/**
 * @author: zhou shuai
 * @date: 2024/3/1 23:19
 * @version: v1
 */
@Slf4j
@Service("dingTalkService")
@RequiredArgsConstructor
public class DingTalkServiceImpl implements IDingTalkService {

    private final DingerSender dingerSender;

    /**
     * RocketMQ 企微告警
     *
     * @author: zhou shuai
     * @date: 2024/3/1 23:21
     * @param: pushAlterDTO
     */
    @Override
    public void rocketmqAlarm(PushAlterDTO pushAlterDTO) {
        String alarmTitle = JobTypeEnum.getEnumByValue(pushAlterDTO.getAlarmType()).getCodeDesc();
        StringBuilder mdBuilder = new StringBuilder();
        String titleContent = StrUtil.format("<font color=\"warning\">【{}】</font>请相关同事注意。\n相关参数如下:\n", pushAlterDTO.getAlarmContent());
        mdBuilder.append(titleContent);
        Field[] fields = ReflectUtil.getFields(pushAlterDTO.getClass());
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            Object fieldValue = ReflectUtil.getFieldValue(pushAlterDTO, field);
            if (Objects.isNull(fieldValue)) {
                continue;
            }
            String fieldContent = StrUtil.format(">**{}:** <font color=\"comment\">{}</font> \n", fieldName, fieldValue);
            mdBuilder.append(fieldContent);
        }
        DingerRequest dingerRequest = DingerRequest.request(mdBuilder.toString(), alarmTitle);
        dingerSender.send(MessageSubType.MARKDOWN, dingerRequest);
    }

}
