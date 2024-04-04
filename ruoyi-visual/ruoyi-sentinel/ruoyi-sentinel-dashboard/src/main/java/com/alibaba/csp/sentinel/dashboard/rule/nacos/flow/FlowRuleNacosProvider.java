/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule.nacos.flow;

import com.alibaba.csp.sentinel.dashboard.config.properties.NacosProperties;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
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
 * Nacos流控规则
 *
 * @author shuai.zhou
 */
@Component("flowRuleNacosProvider")
@RequiredArgsConstructor
public class FlowRuleNacosProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final Converter<String, List<FlowRuleEntity>> converter;

    private final NacosProperties nacosProperties;


    /**
     * 获取Nacos流控规则
     *
     * @author: zhou shuai
     * @date: 2024/2/8 22:13
     * @param: appName
     * @return: java.util.List<com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity>
     */
    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        String flowDataId = NacosConfigUtil.getFlowDataId(appName);
        String groupId = nacosProperties.getGroupId();
        String rules = configService.getConfig(flowDataId, groupId, timeoutInMills);
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }
}
