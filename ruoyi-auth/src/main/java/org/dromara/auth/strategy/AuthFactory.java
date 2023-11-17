package org.dromara.auth.strategy;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description: 登录策略工厂类(工厂模式)
 * @author: zhou shuai
 * @date: 2023/11/17 10:51
 * @version: v1
 */
@Component
public class AuthFactory {

    @Resource
    private List<IAuthStrategy> authStrategyList = new ArrayList<>();

    private final Map<String, IAuthStrategy> authStrategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        for (IAuthStrategy authStrategy : authStrategyList) {
            String loginType = authStrategy.loginType().getCode();
            authStrategyMap.put(loginType, authStrategy);
        }
    }

    /**
     * 根据登录类型获取实例
     *
     * @author: zhou shuai
     * @date: 2023/11/17 10:53
     * @param: loginType
     * @return: org.dromara.auth.strategy.IAuthStrategy
     */
    public IAuthStrategy getLoginStrategy(String loginType) {
        return authStrategyMap.get(loginType);
    }

}
