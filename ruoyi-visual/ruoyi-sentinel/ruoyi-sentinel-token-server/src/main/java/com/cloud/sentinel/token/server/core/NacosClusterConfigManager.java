package com.cloud.sentinel.token.server.core;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.ConfigType;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.core.entity.enums.MessageSubType;
import com.cloud.sentinel.token.server.config.properties.SentinelNacosProperties;
import com.cloud.sentinel.token.server.entity.ClusterGroupEntity;
import com.cloud.sentinel.token.server.utils.NacosConfigUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @author shuai.zhou
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class NacosClusterConfigManager {

    private static final long timeoutInMills = 3000;

    private final ConfigService configService;

    private final DingerSender dingerSender;

    private final SentinelNacosProperties sentinelNacosProperties;

    /**
     * 成为master的tokenServer修改namespace中的集群配置
     *
     * @author: zhou shuai
     * @date: 2024/2/8 23:41
     * @param: ip     tokenServer ip
     * @param: port   tokenServer port
     */
    public void changeMasterTokenServerAddress(String ip, Integer port) throws Exception {
        String rules = configService.getConfig(NacosConfigUtil.getTokenServerClusterDataId(), sentinelNacosProperties.getGroupId(), timeoutInMills);
        if (StrUtil.isBlank(rules)) {
            return;
        }
        publishMasterTokenServerAddress(rules, ip, port);
    }

    /**
     * 将master TokenServer的配置写入namespace的集群配置中
     *
     * @param rules token-server集群配置内容
     * @param ip    tokenServer ip
     * @param port  tokenServer port
     */
    private void publishMasterTokenServerAddress(String rules, String ip, Integer port) {
        try {
            List<ClusterGroupEntity> groupList = JSON.parseObject(rules, new TypeReference<List<ClusterGroupEntity>>() {

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

            configService.publishConfig(NacosConfigUtil.getTokenServerClusterDataId(), sentinelNacosProperties.getGroupId(), JSON.toJSONString(groupList), ConfigType.JSON.getType());
            if (log.isDebugEnabled()) {
                log.debug("Token Server 地址修改成功，dataId:【" + NacosConfigUtil.getTokenServerClusterDataId() + "】");
            }
        } catch (Exception e) {
            log.error("Token Server 地址修改失败，dataId:【" + NacosConfigUtil.getTokenServerClusterDataId() + "】");
            //发送企业微信告警
            dingerSender.send(MessageSubType.TEXT, DingerRequest.request("Token Server 地址修改失败，dataId:【" + NacosConfigUtil.getTokenServerClusterDataId() + "】"));
        }
    }

}
