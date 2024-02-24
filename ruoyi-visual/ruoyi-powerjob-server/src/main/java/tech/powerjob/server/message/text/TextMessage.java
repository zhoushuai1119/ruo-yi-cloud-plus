package tech.powerjob.server.message.text;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.github.jaemon.dinger.core.entity.DingerRequest;
import com.github.jaemon.dinger.support.CustomMessage;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;

/**
 * 自定义TextMessage
 *
 * @author: zhou shuai
 * @date: 2024/2/23 17:46
 */
@Component("textMessage")
public class TextMessage implements CustomMessage {

    @Override
    public String message(String projectId, DingerRequest request) {
        String content = request.getContent();
        String env = SpringUtil.getActiveProfile();
        String title = StrUtil.isNotBlank(request.getTitle()) ? request.getTitle() : "PowerJob通知";
        return MessageFormat.format(
            "【{0}】 {1}\n- 环境: 【{2}】.\n- 内容: {3}.\n- 时间: {4}",
            title, projectId, env, content, DateUtil.now());
    }

}
