package com.alibaba.csp.sentinel.dashboard.rule.apollo.degrade;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
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
 * Apollo降级规则
 *
 * @author shuai.zhou
 */
@Component("degradeRuleApolloProvider")
@RequiredArgsConstructor
public class DegradeRuleApolloProvider implements DynamicRuleProvider<List<DegradeRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<String, List<DegradeRuleEntity>> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 获取Apollo降级规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:04
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity>
     */
    @Override
    public List<DegradeRuleEntity> getRules(String appName) {
        String env = SpringUtil.getActiveProfile();
        String flowDataId = ApolloConfigUtil.getDegradeDataId(appName);
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
