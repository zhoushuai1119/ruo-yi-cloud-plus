package org.dromara.common.mybatis.config;

import org.dromara.common.mybatis.interceptor.DynamicDatasourceInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author: zhou shuai
 * @date: 2023/11/13 22:24
 * @version: v1
 */
@Configuration
public class DynamicDatasourceConfiguration implements WebMvcConfigurer {

    /**
     * 多数据源拦截器
     */
    @Bean
    public DynamicDatasourceInterceptor dynamicDatasourceInterceptor(){
        return new DynamicDatasourceInterceptor();
    }

    /**
     * 注册多数据源配置拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(dynamicDatasourceInterceptor()).addPathPatterns("/**");
    }

}
