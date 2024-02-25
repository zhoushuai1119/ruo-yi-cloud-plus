package tech.powerjob.server.alarm;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.cloud.alarm.dinger.DingerSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.powerjob.server.extension.alarm.Alarm;
import tech.powerjob.server.extension.alarm.AlarmTarget;
import tech.powerjob.server.extension.alarm.Alarmable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 企微告警
 *
 * @author: zhou shuai
 * @date: 2024/2/23 16:38
 */
@Slf4j
@Service("weComAlarmService")
@RequiredArgsConstructor
public class WeComAlarmService implements Alarmable {

    private final DingerSender dingerSender;

    @Override
    public void onFailed(Alarm alarm, List<AlarmTarget> alarmTargetList) {
        List<String> phoneList = new ArrayList<>();
        if (CollUtil.isNotEmpty(alarmTargetList)) {
            phoneList = alarmTargetList.stream()
                .map(AlarmTarget::getPhone)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toList());
        }
    }

}
