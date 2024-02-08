package com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.correct;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.RuleEntity;
import com.alibaba.csp.sentinel.slots.block.Rule;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityRule;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * @author: zhou shuai
 * @description: 重写 AuthorityRuleEntity
 * @date: 2022/10/22 10:04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthorityRuleCorrectEntity extends CommonEntity implements RuleEntity {

    @Serial
    private static final long serialVersionUID = 6712872451639320342L;

    /********************AuthorityRule属性*********************************/
    /**
     * 0-白名单; 1-黑名单
     */
    private int strategy = 0;


    @Override
    public Rule toRule() {
        AuthorityRule rule = new AuthorityRule();
        return rule;
    }

}
