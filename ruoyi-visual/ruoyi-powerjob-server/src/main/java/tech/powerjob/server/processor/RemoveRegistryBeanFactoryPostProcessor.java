package tech.powerjob.server.processor;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 移除BeanDefinition
 *
 * @author: zhou shuai
 * @date: 2024/2/23 16:38
 */
@Component
public class RemoveRegistryBeanFactoryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * 需要移除的BeanDefinitionName集合
     */
    private static final List<String> REMOVE_BEAN_DEFINITION_NAME_LIST = List.of("dingTalkAlarmService", "webHookAlarmService");

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        for (String beanDefinitionName : REMOVE_BEAN_DEFINITION_NAME_LIST) {
            if (beanDefinitionRegistry.containsBeanDefinition(beanDefinitionName)) {
                beanDefinitionRegistry.removeBeanDefinition(beanDefinitionName);
            }
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }

}
