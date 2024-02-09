package com.alibaba.csp.sentinel.dashboard.rule.apollo.cluster;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
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
 * Apollo集群流控规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("clusterGroupApolloPublisher")
@RequiredArgsConstructor
public class ClusterGroupApolloPublisher implements DynamicRulePublisher<List<ClusterAppAssignMap>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<List<ClusterAppAssignMap>, String> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 推送集群流控规则至Apollo
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:00
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<ClusterAppAssignMap> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        String env = SpringUtil.getActiveProfile();
        // 创建配置
        String flowDataId = ApolloConfigUtil.getClusterGroupDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(converter.convert(rules));
        openItemDTO.setComment(app + "集群流控");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getUser());
        apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getClusterName(), openItemDTO);

        // 发布配置
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("publish ClusterGroup config");
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
        namespaceReleaseDTO.setReleaseTitle("publish ClusterGroup config");
        apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getClusterName(), namespaceReleaseDTO);
        log.info("publish app:{} ClusterGroup success rules: {}", app, JSON.toJSONString(rules));
    }

}
