package com.alibaba.csp.sentinel.dashboard.rule.nacos.auth;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Nacos授权规则
 *
 * @author shuai.zhou
 */
@Component("authorityRuleNacosProvider")
@RequiredArgsConstructor
public class AuthorityRuleNacosProvider implements DynamicRuleProvider<List<AuthorityRuleEntity>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<AuthorityRuleCorrectEntity>> converter;

    private final NacosProperties nacosProperties;


    /**
     * 获取Nacos授权规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 21:29
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity>
     */
    @Override
    public List<AuthorityRuleEntity> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getAuthorityDataId(appName);
        String groupId = nacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        List<AuthorityRuleCorrectEntity> entityList = converter.convert(rules);
        return entityList.stream().map(rule -> {
            AuthorityRule authorityRule = new AuthorityRule();
            BeanUtils.copyProperties(rule, authorityRule);
            AuthorityRuleEntity entity = AuthorityRuleEntity.fromAuthorityRule(rule.getApp(), rule.getIp(), rule.getPort(), authorityRule);
            entity.setId(rule.getId());
            return entity;
        }).collect(Collectors.toList());
    }

}
