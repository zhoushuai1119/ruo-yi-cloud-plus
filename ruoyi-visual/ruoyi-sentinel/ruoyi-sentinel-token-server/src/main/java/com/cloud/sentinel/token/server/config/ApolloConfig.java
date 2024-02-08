/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloud.sentinel.token.server.config;

import com.cloud.sentinel.token.server.config.properties.ApolloProperties;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Apollo 配置类
 *
 * @author shuai.zhou
 */
@Configuration
@EnableConfigurationProperties(ApolloProperties.class)
public class ApolloConfig {

    @Bean
    public ApolloOpenApiClient apolloOpenApiClient(ApolloProperties apolloProperties) {
        ApolloOpenApiClient client = ApolloOpenApiClient.newBuilder()
            .withPortalUrl(apolloProperties.getMeta())
            .withToken(apolloProperties.getToken())
            .build();
        return client;
    }

}
