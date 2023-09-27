package org.dromara.demo.service.impl;

import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.dromara.demo.service.ITestSeataService;
import org.dromara.resource.api.RemoteResourceSeataService;
import org.dromara.system.api.RemoteSystemSeataService;
import org.springframework.stereotype.Service;

/**
 * 测试分布式事务Seata Service业务层处理
 *
 * @author Zhou Shuai
 */
@Service
@Slf4j
public class TestSeataServiceImpl implements ITestSeataService {

    @DubboReference
    private RemoteSystemSeataService remoteSystemSeataService;

    @DubboReference
    private RemoteResourceSeataService remoteResourceSeataService;

    /**
     * 测试分布式事务Seata
     *
     * @GlobalTransactional 开启全局事务
     */
    @Override
    @GlobalTransactional(rollbackFor = Exception.class)
    public void seataTest(String value) {
        log.info("开始远程调用ruoyi-system服务.....");
        remoteSystemSeataService.seataTest(value);
        log.info("======================================================");
        log.info("开始远程调用ruoyi-resource服务.....");
        remoteResourceSeataService.seataTest(value);
    }

}
