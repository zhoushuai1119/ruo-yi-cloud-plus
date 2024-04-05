package com.alibaba.csp.sentinel.dashboard.rule.nacos.auth;

import com.alibaba.csp.sentinel.dashboard.config.properties.SentinelNacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.AuthorityRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Nacos授权规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("authorityRuleNacosPublisher")
@RequiredArgsConstructor
public class AuthorityRuleNacosPublisher implements DynamicRulePublisher<List<AuthorityRuleEntity>> {

    private final ConfigService configService;

    private final Converter<List<AuthorityRuleCorrectEntity>, String> converter;

    private final SentinelNacosProperties sentinelNacosProperties;

    /**
     * 推送授权规则至Nacos
     *
     * @author: zhou shuai
     * @date: 2024/2/8 21:31
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<AuthorityRuleEntity> rules) throws Exception {
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

        // 创建配置
        String flowDataId = NacosConfigUtil.getAuthorityDataId(app);
        String groupId = sentinelNacosProperties.getGroupId();
        configService.publishConfig(flowDataId, groupId, converter.convert(authorityRuleList), ConfigType.JSON.getType());
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
