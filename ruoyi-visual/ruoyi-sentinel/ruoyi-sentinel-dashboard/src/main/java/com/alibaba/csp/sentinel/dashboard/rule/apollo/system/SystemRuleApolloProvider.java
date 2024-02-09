package com.alibaba.csp.sentinel.dashboard.rule.apollo.system;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
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
import java.util.Objects;

/**
 * Apollo系统规则
 *
 * @author shuai.zhou
 */
@Component("systemRuleApolloProvider")
@RequiredArgsConstructor
public class SystemRuleApolloProvider implements DynamicRuleProvider<List<SystemRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<String, List<SystemRuleEntity>> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 获取Apollo系统规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:45
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity>
     */
    @Override
    public List<SystemRuleEntity> getRules(String appName) {
        String env = SpringUtil.getActiveProfile();
        String flowDataId = ApolloConfigUtil.getSystemDataId(appName);
        OpenNamespaceDTO openNamespaceDTO;
        // 将gateway规则单独放入gatewayNamespace
        if (Objects.equals(appName, apolloProperties.getGatewayServerName())) {
            openNamespaceDTO = apolloOpenApiClient.getNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getGatewayNamespace());
        } else {
            openNamespaceDTO = apolloOpenApiClient.getNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace());
        }
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
