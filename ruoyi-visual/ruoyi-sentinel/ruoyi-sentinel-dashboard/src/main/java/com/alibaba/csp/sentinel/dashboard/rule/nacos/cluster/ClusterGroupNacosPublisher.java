package com.alibaba.csp.sentinel.dashboard.rule.nacos.cluster;

import com.alibaba.csp.sentinel.dashboard.config.properties.SentinelNacosProperties;
import com.alibaba.csp.sentinel.dashboard.domain.cluster.request.ClusterAppAssignMap;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.dashboard.util.NacosConfigUtil;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Nacos集群流控规则
 *
 * @author shuai.zhou
 */
@Slf4j
@Component("clusterGroupNacosPublisher")
@RequiredArgsConstructor
public class ClusterGroupNacosPublisher implements DynamicRulePublisher<List<ClusterAppAssignMap>> {

    private final ConfigService configService;

    private final Converter<List<ClusterAppAssignMap>, String> converter;

    private final SentinelNacosProperties sentinelNacosProperties;


    /**
     * 推送集群流控规则至Nacos
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:00
     * @param: app
     * @param: rules
     */
    @Override
    public void publish(String app, List<ClusterAppAssignMap> rules) throws Exception {
        AssertUtil.notEmpty(app, "app name cannot be empty");
        if (rules == null) {
            return;
        }
        // 创建配置
        String flowDataId = NacosConfigUtil.getClusterGroupDataId(app);
        String groupId = sentinelNacosProperties.getGroupId();
        configService.publishConfig(flowDataId, groupId, converter.convert(rules), ConfigType.JSON.getType());
        log.info("publish app:{} ClusterGroup success rules: {}", app, JSON.toJSONString(rules));
    }

}
