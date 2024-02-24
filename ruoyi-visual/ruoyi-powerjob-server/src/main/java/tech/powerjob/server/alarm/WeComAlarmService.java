package tech.powerjob.server.alarm;

import com.github.jaemon.dinger.DingerSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tech.powerjob.server.extension.alarm.Alarm;
import tech.powerjob.server.extension.alarm.AlarmTarget;
import tech.powerjob.server.extension.alarm.Alarmable;

import java.util.List;

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
    public void onFailed(Alarm alarm, List<AlarmTarget> list) {

    }

}
