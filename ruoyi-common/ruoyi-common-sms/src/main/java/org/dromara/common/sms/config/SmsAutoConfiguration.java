package org.dromara.common.sms.config;

import org.dromara.common.redis.config.RedissonAutoConfiguration;
import org.dromara.common.sms.core.dao.PlusSmsDao;
import org.dromara.sms4j.api.dao.SmsDao;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * 短信配置类
 *
 * @author shuai.zhou
 */
@AutoConfiguration(after = {RedissonAutoConfiguration.class})
public class SmsAutoConfiguration {

    @Primary
    @Bean
    public SmsDao smsDao() {
        return new PlusSmsDao();
    }

}
