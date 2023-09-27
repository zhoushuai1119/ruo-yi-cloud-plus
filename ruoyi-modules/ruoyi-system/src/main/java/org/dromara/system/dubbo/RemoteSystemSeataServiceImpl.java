package org.dromara.system.dubbo;

import io.seata.core.context.RootContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.system.api.RemoteSystemSeataService;
import org.dromara.system.domain.SysSeataTest;
import org.dromara.system.mapper.SysSeataTestMapper;
import org.springframework.stereotype.Service;

/**
 * 系统Seata服务
 *
 * @author Michelle.Chung
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteSystemSeataServiceImpl implements RemoteSystemSeataService {

    private final SysSeataTestMapper sysSeataTestMapper;

    /**
     * ruoyi-system seata 测试
     */
    @Override
    public void seataTest(String value) {
        log.info("ruoyi-system xid:{}", RootContext.getXID());
        SysSeataTest seataTest = new SysSeataTest();
        seataTest.setValue(value);
        sysSeataTestMapper.insert(seataTest);
    }

}
