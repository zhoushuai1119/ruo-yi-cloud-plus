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

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.ApolloConfigUtil;
import com.alibaba.csp.sentinel.dashboard.config.properties.ApolloProperties;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Apollo网关流控规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("gatewayFlowRuleApolloPublisher")
@RequiredArgsConstructor
public class GatewayFlowRuleApolloPublisher implements DynamicRulePublisher<List<GatewayFlowRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<List<GatewayFlowRuleEntity>, String> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 推送网关流控规则至Apollo
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:36
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<GatewayFlowRuleEntity> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        filterField(rules);
        String env = SpringUtil.getActiveProfile();
        // 创建配置
        String flowDataId = ApolloConfigUtil.getGatewayFlowDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(converter.convert(rules));
        openItemDTO.setComment(app + "网关流控规则");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getUser());
        apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getGatewayNamespace(), openItemDTO);

        // 发布配置
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("publish GatewayFlowRule config");
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
        namespaceReleaseDTO.setReleaseTitle("publish GatewayFlowRule config");
        apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getGatewayNamespace(), namespaceReleaseDTO);
        log.info("publish app:{} GatewayFlowRule success rules: {}", app, JSON.toJSONString(rules));
    }

    /**
     * 过滤不必要的字段
     *
     * @param rules
     */
    private void filterField(List<GatewayFlowRuleEntity> rules) {
        // 对不必要的信息进行过滤
        for (GatewayFlowRuleEntity rule : rules) {
            rule.setGmtCreate(null);
            rule.setGmtModified(null);
            rule.setIp(null);
            rule.setPort(null);
        }
    }

}