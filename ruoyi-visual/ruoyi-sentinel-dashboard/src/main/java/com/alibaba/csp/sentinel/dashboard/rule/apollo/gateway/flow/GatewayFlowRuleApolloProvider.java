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
package com.alibaba.csp.sentinel.dashboard.rule.apollo.gateway.flow;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.ApolloConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 01398395
 * @since 1.5.0
 */
@Component("gatewayFlowRuleApolloProvider")
public class GatewayFlowRuleApolloProvider implements DynamicRuleProvider<List<GatewayFlowRuleEntity>> {

    @Resource
    private ApolloOpenApiClient apolloOpenApiClient;
    @Resource
    private Converter<String, List<GatewayFlowRuleEntity>> converter;
    @Value("${app.id}")
    private String appId;
    @Value("${spring.profiles.active}")
    private String env;
    @Value("${apollo.clusterName}")
    private String clusterName;
    @Value("${apollo.gateway.namespaceName}")
    private String gatewayNamespaceName;

    @Override
    public List<GatewayFlowRuleEntity> getRules(String appName) {
        String flowDataId = ApolloConfigUtil.getGatewayFlowDataId(appName);
        OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(appId, env, clusterName, gatewayNamespaceName);
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
