package org.dromara.monitor.rocketmq.message;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.cloud.alarm.dinger.constant.DingerConstant;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.support.CustomMessage;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 自定义MarkDownMessage
 *
 * @author: zhou shuai
 * @date: 2024/2/23 17:46
 */
@Component(DingerConstant.MARKDOWN_MESSAGE)
public class MarkDownMessage implements CustomMessage {

    @Override
    public String message(String projectId, DingerRequest request) {
        String content = request.getContent();
        String env = SpringUtil.getActiveProfile();
        String title = StrUtil.isNotBlank(request.getTitle()) ? request.getTitle() : "PowerJob通知";
        return MessageFormat.format(
            "#### {0} \n -项目名称: {1}\n -环境: {2}\n -时间: {3}\n -内容: {4}",
            title, projectId, env, DateUtil.now(), content);
    }

}
