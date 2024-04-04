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
package com.alibaba.csp.sentinel.dashboard.controller;

import com.alibaba.csp.sentinel.dashboard.auth.AuthAction;
import com.alibaba.csp.sentinel.dashboard.auth.AuthService.PrivilegeType;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.AuthorityRuleEntity;
import com.alibaba.csp.sentinel.dashboard.domain.Result;
import com.alibaba.csp.sentinel.dashboard.repository.rule.RuleRepository;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.slots.block.RuleConstant;
import com.alibaba.csp.sentinel.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author shuai.zhou
 * @since 0.2.1
 */
@RestController
@RequestMapping(value = "/authority")
@Slf4j
public class AuthorityRuleController {

    @Resource
    private RuleRepository<AuthorityRuleEntity, Long> repository;
    @Resource
    private DynamicRuleProvider<List<AuthorityRuleEntity>> authorityRuleNacosProvider;
    @Resource
    private DynamicRulePublisher<List<AuthorityRuleEntity>> authorityRuleNacosPublisher;

    @GetMapping("/rules")
    @AuthAction(PrivilegeType.READ_RULE)
    public Result<List<AuthorityRuleEntity>> apiQueryAllRulesForMachine(@RequestParam String app) {
        if (StringUtil.isEmpty(app)) {
            return Result.ofFail(-1, "app cannot be null or empty");
        }
        try {
            List<AuthorityRuleEntity> rules = authorityRuleNacosProvider.getRules(app);
            rules = repository.saveAll(rules);
            return Result.ofSuccess(rules);
        } catch (Throwable throwable) {
            log.error("Error when querying authority rules", throwable);
            return Result.ofFail(-1, throwable.getMessage());
        }
    }

    private <R> Result<R> checkEntityInternal(AuthorityRuleEntity entity) {
        if (entity == null) {
            return Result.ofFail(-1, "bad rule body");
        }
        if (StringUtil.isBlank(entity.getApp())) {
            return Result.ofFail(-1, "app can't be null or empty");
        }
        if (entity.getRule() == null) {
            return Result.ofFail(-1, "rule can't be null");
        }
        if (StringUtil.isBlank(entity.getResource())) {
            return Result.ofFail(-1, "resource name cannot be null or empty");
        }
        if (StringUtil.isBlank(entity.getLimitApp())) {
            return Result.ofFail(-1, "limitApp should be valid");
        }
        if (entity.getStrategy() != RuleConstant.AUTHORITY_WHITE
            && entity.getStrategy() != RuleConstant.AUTHORITY_BLACK) {
            return Result.ofFail(-1, "Unknown strategy (must be blacklist or whitelist)");
        }
        return null;
    }

    @PostMapping("/rule")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<AuthorityRuleEntity> apiAddAuthorityRule(@RequestBody AuthorityRuleEntity entity) {
        Result<AuthorityRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(null);
        Date date = new Date();
        entity.setGmtCreate(date);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
        } catch (Throwable throwable) {
            log.error("Failed to add authority rule", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        if (!publishRules(entity.getApp())) {
            log.info("Publish authority rules failed after rule add");
        }
        return Result.ofSuccess(entity);
    }

    @PutMapping("/rule/{id}")
    @AuthAction(PrivilegeType.WRITE_RULE)
    public Result<AuthorityRuleEntity> apiUpdateParamFlowRule(@PathVariable("id") Long id,
                                                              @RequestBody AuthorityRuleEntity entity) {
        if (id == null || id <= 0) {
            return Result.ofFail(-1, "Invalid id");
        }
        Result<AuthorityRuleEntity> checkResult = checkEntityInternal(entity);
        if (checkResult != null) {
            return checkResult;
        }
        entity.setId(id);
        Date date = new Date();
        entity.setGmtCreate(null);
        entity.setGmtModified(date);
        try {
            entity = repository.save(entity);
            if (entity == null) {
                return Result.ofFail(-1, "Failed to save authority rule");
            }
        } catch (Throwable throwable) {
            log.error("Failed to save authority rule", throwable);
            return Result.ofThrowable(-1, throwable);
        }
        if (!publishRules(entity.getApp())) {
            log.info("Publish authority rules failed after rule update");
        }
        return Result.ofSuccess(entity);
    }

    @DeleteMapping("/rule/{id}")
    @AuthAction(PrivilegeType.DELETE_RULE)
    public Result<Long> apiDeleteRule(@PathVariable("id") Long id) {
        if (id == null) {
            return Result.ofFail(-1, "id cannot be null");
        }
        AuthorityRuleEntity oldEntity = repository.findById(id);
        if (oldEntity == null) {
            return Result.ofSuccess(null);
        }
        try {
            repository.delete(id);
        } catch (Exception e) {
            return Result.ofFail(-1, e.getMessage());
        }
        if (!publishRules(oldEntity.getApp())) {
            log.error("Publish authority rules failed after rule delete");
        }
        return Result.ofSuccess(id);
    }

    private boolean publishRules(String app) {
        List<AuthorityRuleEntity> rules = repository.findAllByApp(app);
        try {
            authorityRuleNacosPublisher.publish(app, rules);
            return true;
        } catch (Exception e) {
            log.error("Publish authority rules failed");
            return false;
        }
    }
}
