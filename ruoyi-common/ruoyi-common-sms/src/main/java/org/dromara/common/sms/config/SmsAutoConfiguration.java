package org.dromara.common.sms.config;

import org.dromara.common.core.factory.YmlPropertySourceFactory;
import org.dromara.common.redisson.config.RedissonConfiguration;
import org.dromara.common.sms.core.dao.PlusSmsDao;
import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

/**
 * 短信配置类
 *
 * @author shuai.zhou
 */
@AutoConfiguration(after = {RedissonConfiguration.class})
@PropertySource(value = "classpath:common-sms.yml", factory = YmlPropertySourceFactory.class)
public class SmsAutoConfiguration {

    @Primary
    @Bean
    public SmsDao smsDao() {
        return new PlusSmsDao();
    }

}
