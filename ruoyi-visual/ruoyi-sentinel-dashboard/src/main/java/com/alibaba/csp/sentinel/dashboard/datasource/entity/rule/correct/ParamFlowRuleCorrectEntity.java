package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowClusterConfig;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowItem;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: zhou shuai
 * @description: 重写 ParamFlowRuleEntity
 * @date: 2022/10/22 10:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ParamFlowRuleCorrectEntity extends CommonEntity implements RuleEntity {

    @Serial
    private static final long serialVersionUID = 3893839008285289332L;

    /********************ParamFlowRule属性*********************************/
    private int grade = 1;
    private Integer paramIdx;
    private double count;
    private int controlBehavior = 0;
    private int maxQueueingTimeMs = 0;
    private int burstCount = 0;
    private long durationInSec = 1L;
    private List<ParamFlowItem> paramFlowItemList = new ArrayList();
    private Map<Object, Integer> hotItems = new HashMap();
    private boolean clusterMode = false;
    private ParamFlowClusterConfig clusterConfig;


    @Override
    public Rule toRule() {
        ParamFlowRule rule = new ParamFlowRule();
        return rule;
    }

}
