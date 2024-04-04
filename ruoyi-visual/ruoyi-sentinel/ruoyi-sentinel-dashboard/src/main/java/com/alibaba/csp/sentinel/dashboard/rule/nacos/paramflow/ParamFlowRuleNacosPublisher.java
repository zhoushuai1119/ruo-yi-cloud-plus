package com.alibaba.csp.sentinel.dashboard.rule.nacos.paramflow;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.ParamFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct.ParamFlowRuleCorrectEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Nacos热点规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("paramFlowRuleNacosPublisher")
@RequiredArgsConstructor
public class ParamFlowRuleNacosPublisher implements DynamicRulePublisher<List<ParamFlowRuleEntity>> {

    private final ConfigService configService;

    private final Converter<List<ParamFlowRuleCorrectEntity>, String> converter;

    private final NacosProperties nacosProperties;


    /**
     * 推送热点规则至Nacos
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:41
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<ParamFlowRuleEntity> rules) throws Exception {
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

        // 创建配置
        String flowDataId = NacosConfigUtil.getParamFlowDataId(app);
        String groupId = nacosProperties.getGroupId();
        configService.publishConfig(flowDataId, groupId, converter.convert(paramFlowRuleList));
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
