package com.alibaba.csp.sentinel.dashboard.rule.apollo.degrade;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.ApolloUtil;
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
import java.util.Objects;

/**
 * Apollo降级规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("degradeRuleApolloPublisher")
@RequiredArgsConstructor
public class DegradeRuleApolloPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<List<DegradeRuleEntity>, String> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 推送降级规则至Apollo
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:00
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        filterField(rules);
        String spaceName = apolloProperties.getNamespace();
        if (Objects.equals(app, apolloProperties.getGatewayServerName())) {
            spaceName = apolloProperties.getGatewayNamespace();
        }
        String env = SpringUtil.getActiveProfile();
        // 创建配置
        String flowDataId = ApolloUtil.getDegradeDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(converter.convert(rules));
        openItemDTO.setComment(app + "降级规则");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getUser());
        apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), spaceName, openItemDTO);

        // 发布配置
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("publish DegradeRule config");
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
        namespaceReleaseDTO.setReleaseTitle("publish DegradeRule config");
        apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), spaceName, namespaceReleaseDTO);
        log.info("publish app:{} DegradeRule success rules: {}", app, JSON.toJSONString(rules));
    }

    /**
     * 过滤不必要的字段
     *
     * @param rules
     */
    private void filterField(List<DegradeRuleEntity> rules) {
        // 对不必要的信息进行过滤
        for (DegradeRuleEntity rule : rules) {
            rule.setGmtCreate(null);
            rule.setGmtModified(null);
            rule.setIp(null);
            rule.setPort(null);
        }
    }

}
