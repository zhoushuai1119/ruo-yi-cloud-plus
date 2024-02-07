package com.alibaba.csp.sentinel.dashboard.rule.apollo.system;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
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
 * @program: sentinel-parent
 * @description: 热点规则
 * @author shuai.zhou
 * @create: 2020-07-21 17:06
 **/
@Component("systemRuleApolloProvider")
public class SystemRuleApolloProvider implements DynamicRuleProvider<List<SystemRuleEntity>> {
    @Resource
    private ApolloOpenApiClient apolloOpenApiClient;
    @Resource
    private Converter<String, List<SystemRuleEntity>> converter;

    @Value("${app.id}")
    private String appId;
    @Value("${spring.profiles.active}")
    private String env;
    @Value("${apollo.clusterName}")
    private String clusterName;
    @Value("${apollo.namespaceName}")
    private String namespaceName;
    @Value("${apollo.gateway.namespaceName}")
    private String gatewayNamespaceName;

    @Override
    public List<SystemRuleEntity> getRules(String appName) {
        String flowDataId = ApolloConfigUtil.getSystemDataId(appName);
        OpenNamespaceDTO openNamespaceDTO;
        if (ApolloConfigUtil.isGatewayAppName(appName)) {
            openNamespaceDTO = apolloOpenApiClient.getNamespace(appId, env, clusterName, gatewayNamespaceName);
        } else {
            openNamespaceDTO = apolloOpenApiClient.getNamespace(appId, env, clusterName, namespaceName);
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
