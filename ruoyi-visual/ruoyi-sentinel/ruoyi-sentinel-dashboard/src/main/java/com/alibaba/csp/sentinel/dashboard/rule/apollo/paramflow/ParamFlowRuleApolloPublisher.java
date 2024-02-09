package com.alibaba.csp.sentinel.dashboard.rule.apollo.paramflow;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.ParamFlowRuleCorrectEntity;
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
 * Apollo热点规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("paramFlowRuleApolloPublisher")
@RequiredArgsConstructor
public class ParamFlowRuleApolloPublisher implements DynamicRulePublisher<List<ParamFlowRuleEntity>> {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Converter<List<ParamFlowRuleCorrectEntity>, String> converter;

    private final ApolloProperties apolloProperties;


    /**
     * 推送热点规则至Apollo
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:41
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        filterField(rules);

        //  转换
        List<ParamFlowRuleCorrectEntity> paramFlowRuleList = rules.stream().map(rule -> {
            ParamFlowRuleCorrectEntity entity = new ParamFlowRuleCorrectEntity();
            BeanUtils.copyProperties(rule, entity);
            return entity;
        }).collect(Collectors.toList());

        String env = SpringUtil.getActiveProfile();
        // 创建配置
        String flowDataId = ApolloConfigUtil.getParamFlowDataId(app);
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(flowDataId);
        openItemDTO.setValue(converter.convert(paramFlowRuleList));
        openItemDTO.setComment(app + "热点规则");
        openItemDTO.setDataChangeCreatedBy(apolloProperties.getUser());
        apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), openItemDTO);

        // 发布配置
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(true);
        namespaceReleaseDTO.setReleaseComment("publish ParamFlowRule config");
        namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
        namespaceReleaseDTO.setReleaseTitle("publish ParamFlowRule config");
        apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), namespaceReleaseDTO);
        log.info("publish app:{} ParamFlowRule success rules: {}", app, JSON.toJSONString(rules));
    }

    /**
     * 过滤不必要的字段
     *
     * @param rules
     */
    private void filterField(List<ParamFlowRuleEntity> rules) {
        // 对不必要的信息进行过滤
        for (ParamFlowRuleEntity rule : rules) {
            rule.setGmtCreate(null);
            rule.setGmtModified(null);
            rule.setIp(null);
            rule.setPort(null);
        }
    }

}
