package org.dromara.common.idempotent.config;

import org.dromara.common.idempotent.aspectj.RepeatSubmitAspect;
import org.dromara.common.redisson.config.RedissonConfiguration;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 幂等功能配置
 *
 * @author shuai.zhou
 */
@AutoConfiguration(after = RedissonConfiguration.class)
public class IdempotentAutoConfiguration {

	@Bean
	public RepeatSubmitAspect repeatSubmitAspect() {
		return new RepeatSubmitAspect();
	}

}
