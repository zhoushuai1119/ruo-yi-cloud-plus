package com.alibaba.csp.sentinel.dashboard.rule.nacos.system;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.alibaba.nacos.api.config.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Nacos系统规则
 *
 * @author shuai.zhou
 */
@Component("systemRuleNacosProvider")
@RequiredArgsConstructor
public class SystemRuleNacosProvider implements DynamicRuleProvider<List<SystemRuleEntity>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<SystemRuleEntity>> converter;

    private final NacosProperties nacosProperties;


    /**
     * 获取Nacos系统规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:45
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.SystemRuleEntity>
     */
    @Override
    public List<SystemRuleEntity> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getSystemDataId(appName);
        String groupId = nacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);

        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
