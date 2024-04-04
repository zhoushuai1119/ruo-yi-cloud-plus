package com.alibaba.csp.sentinel.dashboard.rule.nacos.paramflow;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Nacos热点规则
 *
 * @author shuai.zhou
 */
@Component("paramFlowRuleNacosProvider")
@RequiredArgsConstructor
public class ParamFlowRuleNacosProvider implements DynamicRuleProvider<List<ParamFlowRuleEntity>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<ParamFlowRuleCorrectEntity>> converter;

    private final NacosProperties nacosProperties;


    /**
     * 获取Nacos热点规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:40
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity>
     */
    @Override
    public List<ParamFlowRuleEntity> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getParamFlowDataId(appName);
        String groupId = nacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);
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
