package com.alibaba.csp.sentinel.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.config.properties.SentinelNacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Nacos降级规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("degradeRuleNacosPublisher")
@RequiredArgsConstructor
public class DegradeRuleNacosPublisher implements DynamicRulePublisher<List<DegradeRuleEntity>> {

    private final ConfigService configService;

    private final Converter<List<DegradeRuleEntity>, String> converter;

    private final SentinelNacosProperties sentinelNacosProperties;


    /**
     * 推送降级规则至Nacos
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:00
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<DegradeRuleEntity> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        filterField(rules);
        // 创建配置
        String flowDataId = NacosConfigUtil.getDegradeDataId(app);
        String groupId = sentinelNacosProperties.getGroupId();
        configService.publishConfig(flowDataId, groupId, converter.convert(rules), NacosConfigUtil.getConfigContentType());
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
