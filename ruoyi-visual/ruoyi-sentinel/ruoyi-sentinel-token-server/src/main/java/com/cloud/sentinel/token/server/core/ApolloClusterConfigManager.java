package com.cloud.sentinel.token.server.core;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.core.entity.enums.MessageSubType;
import com.cloud.sentinel.token.server.config.properties.ApolloProperties;
import com.cloud.sentinel.token.server.entity.ClusterGroupEntity;
import com.cloud.sentinel.token.server.utils.ApolloConfigUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author shuai.zhou
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ApolloClusterConfigManager {

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final DingerSender dingerSender;

    private final ApolloProperties apolloProperties;

    /**
     * 成为master的tokenServer修改namespace中的集群配置
     *
     * @author: zhou shuai
     * @date: 2024/2/8 23:41
     * @param: ip     tokenServer ip
     * @param: port   tokenServer port
     */
    public void changeMasterTokenServerAddress(String ip, Integer port) {
        String env = SpringUtil.getActiveProfile();
        // 查询token server nameSpace
        OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace());
        List<OpenItemDTO> itemDTOList = openNamespaceDTO.getItems();
        if (CollectionUtils.isEmpty(itemDTOList)) {
            return;
        }
        // 找到配置了集群限流的item
        Optional<OpenItemDTO> clusterConfigItem =
            itemDTOList.stream().filter(t -> ApolloConfigUtil.getTokenServerRuleKey().equals(t.getKey())).findFirst();
        if (clusterConfigItem.isEmpty()) {
            return;
        }
        publishMasterTokenServerAddress(clusterConfigItem.get(), env, ip, port);
    }

    /**
     * 将master TokenServer的配置写入namespace的集群配置中
     *
     * @param openItemDTO 集群规则所在的item
     * @param env         环境
     * @param ip          tokenServer ip
     * @param port        tokenServer port
     */
    private void publishMasterTokenServerAddress(OpenItemDTO openItemDTO, String env, String ip, Integer port) {
        String value = openItemDTO.getValue();
        if (StrUtil.isEmpty(value)) {
            return;
        }
        try {
            List<ClusterGroupEntity> groupList = JSON.parseObject(value, new TypeReference<List<ClusterGroupEntity>>() {

            });

            if (CollectionUtils.isEmpty(groupList)) {
                return;
            }

            ClusterGroupEntity clusterGroupEntity = groupList.get(0);

            //规则中的tokenServer地址与当前相等，不做处理
            if (Objects.equals(clusterGroupEntity.getIp(), ip) && Objects.equals(clusterGroupEntity.getPort(), port)) {
                return;
            }

            clusterGroupEntity.setIp(ip);
            clusterGroupEntity.setPort(port);

            openItemDTO.setValue(JSON.toJSONString(groupList));
            apolloOpenApiClient.createOrUpdateItem(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), openItemDTO);

            // Release configuration
            NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
            namespaceReleaseDTO.setEmergencyPublish(true);
            namespaceReleaseDTO.setReleaseComment("publish Token Server Config");
            namespaceReleaseDTO.setReleasedBy(apolloProperties.getUser());
            namespaceReleaseDTO.setReleaseTitle("publish Token Server Config");
            apolloOpenApiClient.publishNamespace(apolloProperties.getAppId(), env, apolloProperties.getClusterName(), apolloProperties.getNamespace(), namespaceReleaseDTO);

            if (log.isDebugEnabled()) {
                log.debug("Token Server 地址修改成功，namespaceName:【" + apolloProperties.getNamespace() + "】");
            }
        } catch (Exception e) {
            log.error("Token Server 地址修改失败，namespaceName:【" + apolloProperties.getNamespace() + "】");
            //发送企业微信告警
            dingerSender.send(MessageSubType.TEXT, DingerRequest.request("Token Server 地址修改失败，namespaceName:【" + apolloProperties.getNamespace() + "】"));
        }
    }

}
