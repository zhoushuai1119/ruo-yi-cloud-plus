package org.dromara.common.encrypt.config;

import org.dromara.common.core.utils.SpringUtils;
import org.dromara.common.encrypt.core.EncryptorManager;
import org.dromara.common.encrypt.interceptor.MybatisDecryptInterceptor;
import org.dromara.common.encrypt.interceptor.MybatisEncryptInterceptor;
import org.dromara.common.encrypt.properties.EncryptorProperties;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 加解密配置
 *
 * @author shuai.zhou
 * @version 4.6.0
 */
@AutoConfiguration
@EnableConfigurationProperties(EncryptorProperties.class)
@ConditionalOnProperty(value = "mybatis-encryptor.enable", havingValue = "true")
@ConditionalOnClass(name = {"org.dromara.common.mybatis.config.MybatisPlusConfiguration"})
public class EncryptorAutoConfiguration {

    @Bean
    public EncryptorManager encryptorManager() {
        String typeAliasesPackage = SpringUtils.getProperty("mybatis-plus.typeAliasesPackage");
        return new EncryptorManager(typeAliasesPackage);
    }

    @Bean
    public MybatisEncryptInterceptor mybatisEncryptInterceptor(EncryptorManager encryptorManager, EncryptorProperties properties) {
        return new MybatisEncryptInterceptor(encryptorManager, properties);
    }

    @Bean
    public MybatisDecryptInterceptor mybatisDecryptInterceptor(EncryptorManager encryptorManager, EncryptorProperties properties) {
        return new MybatisDecryptInterceptor(encryptorManager, properties);
    }
}
