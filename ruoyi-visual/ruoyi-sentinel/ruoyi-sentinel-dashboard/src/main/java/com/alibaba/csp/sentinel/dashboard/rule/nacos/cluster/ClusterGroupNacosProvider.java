package com.alibaba.csp.sentinel.dashboard.rule.nacos.cluster;

import com.alibaba.csp.sentinel.dashboard.config.properties.SentinelNacosProperties;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
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
 * Nacos集群流控规则
 *
 * @author shuai.zhou
 */
@Component("clusterGroupNacosProvider")
@RequiredArgsConstructor
public class ClusterGroupNacosProvider implements DynamicRuleProvider<List<ClusterAppAssignMap>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<ClusterAppAssignMap>> converter;

    private final SentinelNacosProperties sentinelNacosProperties;


    /**
     * 获取Nacos集群流控规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 21:57
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap>
     */
    @Override
    public List<ClusterAppAssignMap> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getClusterGroupDataId(appName);
        String groupId = sentinelNacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
