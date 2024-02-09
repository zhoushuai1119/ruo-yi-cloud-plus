package com.alibaba.csp.sentinel.dashboard.rule.apollo.paramflow;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.ApolloConfigUtil;
import com.alibaba.csp.sentinel.dashboard.config.properties.ApolloProperties;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Apollo热点规则
 *
 * @author shuai.zhou
 */
@Component("paramFlowRuleApolloProvider")
@RequiredArgsConstructor
public class ParamFlowRuleApolloProvider implements DynamicRuleProvider<List<ParamFlowRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<String, List<ParamFlowRuleCorrectEntity>> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 获取Apollo热点规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:40
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity>
     */
    @Override
    public List<ParamFlowRuleEntity> getRules(String appName) {
        String env = SpringUtil.getActiveProfile();
        String flowDataId = ApolloConfigUtil.getParamFlowDataId(appName);
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

        List<ParamFlowRuleCorrectEntity> entityList = converter.convert(rules);
        return entityList.stream().map(rule -> {
            ParamFlowRule paramFlowRule = new ParamFlowRule();
            BeanUtils.copyProperties(rule, paramFlowRule);
            ParamFlowRuleEntity entity = ParamFlowRuleEntity.fromParamFlowRule(rule.getApp(), rule.getIp(), rule.getPort(), paramFlowRule);
            entity.setId(rule.getId());
            return entity;
        }).collect(Collectors.toList());
    }

}
