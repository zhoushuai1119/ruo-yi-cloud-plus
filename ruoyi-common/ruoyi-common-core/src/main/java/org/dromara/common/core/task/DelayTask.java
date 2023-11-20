package org.dromara.common.core.task;

import cn.hutool.cron.timingwheel.SystemTimer;
import cn.hutool.cron.timingwheel.TimerTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;

/**
 * @description:
 * @author: zhou shuai
 * @date: 2023/11/20 14:56
 * @version: v1
 */
@Slf4j
public class DelayTask implements InitializingBean, DisposableBean {

    private final SystemTimer systemTimer;

    public DelayTask() {
        this.systemTimer = new SystemTimer();
    }

    @Override
    public void afterPropertiesSet() {
        systemTimer.start();
        log.info("delay task start...");
    }

    @Override
    public void destroy() {
        systemTimer.stop();
        log.info("delay task stop...");
    }

    /**
     * 添加任务
     *
     * @author: zhou shuai
     * @date: 2023/11/20 15:03
     * @param: runnable
     * @param: delayMs 延迟时间(单位:毫秒)
     */
    public void addTask(Runnable runnable, long delayMs) {
        systemTimer.addTask(new TimerTask(runnable, delayMs));
    }

}
