package com.alibaba.csp.sentinel.dashboard.rule.apollo.cluster;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.ApolloUtil;
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
 * Apollo集群流控规则
 *
 * @author shuai.zhou
 */
@Component("clusterGroupApolloProvider")
@RequiredArgsConstructor
public class ClusterGroupApolloProvider implements DynamicRuleProvider<List<ClusterAppAssignMap>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<String, List<ClusterAppAssignMap>> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 获取Apollo集群流控规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 21:57
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap>
     */
    @Override
    public List<ClusterAppAssignMap> getRules(String appName) {
        String env = SpringUtil.getActiveProfile();
        String flowDataId = ApolloUtil.getClusterGroupDataId(appName);
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
