package org.dromara.job.processors;

import cn.hutool.json.JSONUtil;
import com.cloud.mq.base.core.CloudMQTemplate;
import com.cloud.mq.base.dto.CloudMQSendResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tech.powerjob.worker.core.processor.ProcessResult;
import tech.powerjob.worker.core.processor.TaskContext;
import tech.powerjob.worker.core.processor.sdk.BasicProcessor;
import tech.powerjob.worker.log.OmsLogger;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author Echo009
 * @since 2022/4/27
 */
@Component
@Slf4j
public class SimpleProcessor implements BasicProcessor {

    @Resource
    private CloudMQTemplate cloudMQTemplate;

    @Override
    public ProcessResult process(TaskContext context) throws Exception {

        OmsLogger logger = context.getOmsLogger();

        String jobParams = Optional.ofNullable(context.getJobParams()).orElse("S");
        logger.info("Current context:{}", context.getWorkflowContext());
        logger.info("Current job params:{}", jobParams);

        CloudMQSendResult cloudMQSendResult = cloudMQTemplate.syncSend(jobParams, "POWERJOB_WORKER", "SimpleProcessor test");
        log.info("cloudMQSendResult is {}", JSONUtil.toJsonStr(cloudMQSendResult));

        // 测试中文问题 #581
        if (jobParams.contains("CN")) {
            return new ProcessResult(true, "任务成功啦！！！");
        }

        return jobParams.contains("F") ? new ProcessResult(false) : new ProcessResult(true, "yeah!");

    }
}
