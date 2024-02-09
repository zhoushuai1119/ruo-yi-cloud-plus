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
package com.alibaba.csp.sentinel.dashboard.rule.apollo.flow;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.ApolloConfigUtil;
import com.alibaba.csp.sentinel.dashboard.config.properties.ApolloProperties;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Apollo流控规则
 *
 * @author shuai.zhou
 */
@Component("flowRuleApolloProvider")
@RequiredArgsConstructor
public class FlowRuleApolloProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<String, List<FlowRuleEntity>> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 获取Apollo流控规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:13
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity>
     */
    @Override
    public List<FlowRuleEntity> getRules(String appName) {
        String env = SpringUtil.getActiveProfile();
        String flowDataId = ApolloConfigUtil.getFlowDataId(appName);
        OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace());
        String rules = openNamespaceDTO
            .getItems()
            .stream()
            .filter(p -> p.getKey().equals(flowDataId))
            .map(OpenItemDTO::getValue)
            .findFirst()
            .orElse("");

        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
