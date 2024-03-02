package org.dromara.demo.controller;

import com.cloud.mq.base.core.CloudMQTemplate;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.domain.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * rocketmq 演示案例
 *
 * @author shuai.zhou
 * @date 2021-05-30
 */
@RestController
@RequestMapping("/rocketmq")
@Slf4j
public class TestRocketMQController {

    @Resource
    private CloudMQTemplate cloudMQTemplate;

    /**
     * 发送消息测试
     */
    @GetMapping("/send/msg")
    public R<Void> sendMsg() {
        for (int i = 0; i < 50; i++) {
            cloudMQTemplate.syncSend("TP_DTO_TEST_TOPIC", "tag1", "11111111");
        }
        return R.ok();
    }

}
