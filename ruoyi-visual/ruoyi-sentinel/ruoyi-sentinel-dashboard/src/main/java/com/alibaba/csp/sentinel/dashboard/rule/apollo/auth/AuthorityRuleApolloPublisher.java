package com.alibaba.csp.sentinel.dashboard.rule.apollo.auth;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.AuthorityRuleCorrectEntity;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Apollo授权规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("authorityRuleApolloPublisher")
@RequiredArgsConstructor
public class AuthorityRuleApolloPublisher implements DynamicRulePublisher<List<AuthorityRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<List<AuthorityRuleCorrectEntity>, String> converter;

    private final ApolloProperties apolloProperties;

    /**
     * 推送授权规则至Apollo
     *
     * @author: zhou shuai
     * @date: 2024/2/8 21:31
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<AuthorityRuleEntity> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        filterField(rules);

        //  转换
        List<AuthorityRuleCorrectEntity> authorityRuleList = rules.stream().map(rule -> {
            AuthorityRuleCorrectEntity entity = new AuthorityRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());

        String env = SpringUtil.getActiveProfile();
        // 创建配置
        String flowDataId = ApolloConfigUtil.getAuthorityDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(converter.convert(authorityRuleList));
        openItemDTO.setComment(app + "授权规则");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getUser());
        apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), openItemDTO);

        // 发布配置
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("publish authorityRule config");
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
        namespaceReleaseDTO.setReleaseTitle("publish authorityRule config");
        apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), namespaceReleaseDTO);
        log.info("publish app:{} AuthorityRule success rules: {}", app, JSON.toJSONString(rules));
    }

    /**
     * 过滤不必要的字段
     *
     * @param rules
     */
    private void filterField(List<AuthorityRuleEntity> rules) {
        // 对不必要的信息进行过滤
        for (AuthorityRuleEntity rule : rules) {
            rule.setGmtCreate(null);
            rule.setGmtModified(null);
            rule.setIp(null);
            rule.setPort(null);
        }
    }

}
