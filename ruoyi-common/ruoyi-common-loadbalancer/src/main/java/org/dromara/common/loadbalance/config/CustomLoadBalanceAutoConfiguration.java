package org.dromara.common.loadbalance.config;

import org.springframework.cloud.loadbalancer.annotation.LoadBalancerClients;

/**
 * 自定义负载均衡自动配置
 *
 * @author shuai.zhou
 */
@LoadBalancerClients(defaultConfiguration = CustomLoadBalanceClientConfiguration.class)
public class CustomLoadBalanceAutoConfiguration {

}
