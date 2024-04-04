package com.alibaba.csp.sentinel.dashboard.rule.nacos.degrade;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity;
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
 * Nacos降级规则
 *
 * @author shuai.zhou
 */
@Component("degradeRuleNacosProvider")
@RequiredArgsConstructor
public class DegradeRuleNacosProvider implements DynamicRuleProvider<List<DegradeRuleEntity>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<DegradeRuleEntity>> converter;

    private final NacosProperties nacosProperties;


    /**
     * 获取Nacos降级规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:04
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.DegradeRuleEntity>
     */
    @Override
    public List<DegradeRuleEntity> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getDegradeDataId(appName);
        String groupId = nacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
