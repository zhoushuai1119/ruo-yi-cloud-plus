package tech.powerjob.server.alarm;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.core.entity.enums.MessageSubType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.powerjob.server.core.alarm.module.JobInstanceAlarm;
import tech.powerjob.server.core.alarm.module.WorkflowInstanceAlarm;
import tech.powerjob.server.extension.alarm.Alarm;
import tech.powerjob.server.extension.alarm.AlarmTarget;
import tech.powerjob.server.extension.alarm.Alarmable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 企微告警
 * 开发文档: https://developer.work.weixin.qq.com/document/path/91770
 *
 * @author: zhou shuai
 * @date: 2024/2/23 16:38
 */
@Slf4j
@Service("weComAlarmService")
@RequiredArgsConstructor
public class WeComAlarmService implements Alarmable {

    /**
     * 时间戳字段名
     */
    private final static List<String> TIME_FIELD_LIST = List.of("expectedTriggerTime", "actualTriggerTime", "finishedTime");

    private final DingerSender dingerSender;

    @Override
    public void onFailed(Alarm alarm, List<AlarmTarget> alarmTargetList) {
        String title = alarm.fetchTitle();
        StringBuilder mdBuilder = new StringBuilder();
        String titleContent = StrUtil.EMPTY;
        if (alarm instanceof JobInstanceAlarm jobInstanceAlarm) {
            titleContent = StrUtil.format("<font color=\"warning\">【{}】</font>执行失败,请相关同事注意。\n相关参数如下:\n", jobInstanceAlarm.getJobName());
        } else if (alarm instanceof WorkflowInstanceAlarm workflowInstanceAlarm) {
            titleContent = StrUtil.format("<font color=\"warning\">【{}】</font>执行失败,请相关同事注意。\n相关参数如下:\n", workflowInstanceAlarm.getWorkflowName());
        }
        mdBuilder.append(titleContent);
        Field[] fields = ReflectUtil.getFields(alarm.getClass());
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            String fieldName = field.getName();
            Object fieldValue = ReflectUtil.getFieldValue(alarm, field);
            if (Objects.isNull(fieldValue)) {
                continue;
            }
            if (TIME_FIELD_LIST.contains(fieldName)) {
                Date timeFieldValue = new Date(Long.parseLong(String.valueOf(fieldValue)));
                fieldValue = DateUtil.formatDateTime(timeFieldValue);
            }
            String fieldContent = StrUtil.format(">**{}:** <font color=\"comment\">{}</font> \n", fieldName, fieldValue);
            mdBuilder.append(fieldContent);
        }
        DingerRequest dingerRequest = DingerRequest.request(mdBuilder.toString(), title);
        dingerSender.send(MessageSubType.MARKDOWN, dingerRequest);
    }

}
