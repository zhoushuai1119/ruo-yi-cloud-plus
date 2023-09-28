package org.dromara.resource.dubbo;

import cn.hutool.core.util.RandomUtil;
import io.seata.core.context.RootContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.dromara.resource.api.RemoteResourceSeataService;
import org.dromara.resource.domain.ResSeataTest;
import org.dromara.resource.mapper.ResSeataTestMapper;
import org.springframework.stereotype.Service;

/**
 * 资源Seata服务
 *
 * @author Michelle.Chung
 */
@Slf4j
@RequiredArgsConstructor
@Service
@DubboService
public class RemoteResourceSeataServiceImpl implements RemoteResourceSeataService {

    private final ResSeataTestMapper resSeataTestMapper;

    /**
     * ruoyi-resource seata 测试
     */
    @Override
    public void seataTest(String value) {
        log.info("ruoyi-resource xid:{}", RootContext.getXID());
        ResSeataTest seataTest = new ResSeataTest();
        seataTest.setId(RandomUtil.randomLong(4));
        seataTest.setValue(value);
        resSeataTestMapper.insert(seataTest);
    }

}
