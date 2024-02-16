package com.cloud.sentinel.token.server.core;

import com.alibaba.csp.sentinel.cluster.server.ClusterTokenServer;
import com.alibaba.csp.sentinel.cluster.server.SentinelDefaultTokenServer;
import com.alibaba.csp.sentinel.cluster.server.config.ClusterServerConfigManager;
import com.alibaba.csp.sentinel.util.HostNameUtil;
import com.cloud.alarm.dinger.DingerSender;
import com.cloud.alarm.dinger.core.entity.DingerRequest;
import com.cloud.alarm.dinger.core.entity.enums.MessageSubType;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.utils.CloseableUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author shuai.zhou
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TokenServerBootstrap {

    private final ApolloClusterConfigManager apolloClusterConfigManager;

    private final DingerSender dingerSender;

    @Value("${zookeeper.address}")
    private String zkAddress;

    private static final String LOCK_PATH = "/tokenServer";

    private static CuratorFramework ZK_CLIENT;

    private static TokenServerClient TOKEN_SERVER_CLIENT;

    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(r ->
        new Thread(r, "TokenServerCheckMasterThread"));

    @PostConstruct
    public void init() throws Exception {
        ClusterTokenServer tokenServer = new SentinelDefaultTokenServer();
        // Start the server.
        tokenServer.start();

        Integer tokenServerPort = ClusterServerConfigManager.getPort();

        log.info("[sentinel token server]启动成功，port: {}", tokenServerPort);

        //争抢token server master
        ZK_CLIENT = buildZkClient();
        TOKEN_SERVER_CLIENT = new TokenServerClient(ZK_CLIENT, LOCK_PATH);

        ZK_CLIENT.start();
        TOKEN_SERVER_CLIENT.start();

        //每隔1分钟进行一次自检，防止master地址写apollo失败
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            //返回true说明当前实例是leader
            boolean hasLeaderShip = TOKEN_SERVER_CLIENT.getLeaderLatch().hasLeadership();
            if (hasLeaderShip) {
                try {
                    String currentIp = HostNameUtil.getIp();
                    if (log.isDebugEnabled()) {
                        log.debug("[Leader定时检查]" + currentIp + ",当前是TokenServer Master,端口:" + tokenServerPort);
                    }
                    apolloClusterConfigManager.changeMasterTokenServerAddress(currentIp, tokenServerPort);
                } catch (Exception e) {
                    log.error("changeMasterTokenServerAddress fail, error:" + e);
                }
            }
        }, 1000 * 30, 1000 * 60, TimeUnit.MILLISECONDS);

        //当jvm关闭的时候，会执行系统中已经设置的所有通过方法addShutdownHook添加的钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CloseableUtils.closeQuietly(ZK_CLIENT);
            CloseableUtils.closeQuietly(TOKEN_SERVER_CLIENT);
        }));
    }

    private CuratorFramework buildZkClient() {
        return CuratorFrameworkFactory.builder()
            //服务器列表，格式host1:port1,host2:port2
            .connectString(zkAddress)
            //会话超时时间，单位毫秒
            .sessionTimeoutMs(30000)
            //连接创建超时时间，单位毫秒
            .connectionTimeoutMs(30000)
            //重试策略
            .retryPolicy(new ExponentialBackoffRetry(1000, 5))
            //命名空间: 以此为根目录，在多个应用共用一个Zookeeper集群的场景下，这对于实现不同应用之间的相互隔离十分有意义
            .namespace("sentinel")
            .build();
    }

    class TokenServerClient implements Closeable {
        private final String name;

        @Getter
        private final LeaderLatch leaderLatch;

        public TokenServerClient(CuratorFramework client, String path) {
            this.name = HostNameUtil.getIp();
            leaderLatch = new LeaderLatch(client, path);
            leaderLatch.addListener(new LeaderLatchListener() {
                @Override
                public void isLeader() {
                    String currentIp = HostNameUtil.getIp();
                    Integer tokenServerPort = ClusterServerConfigManager.getPort();
                    log.info("Leader选举;【" + currentIp + "】成为了TokenServer Master,端口:" + tokenServerPort);
                    //发送企业微信告警
                    dingerSender.send(MessageSubType.TEXT, DingerRequest.request("Leader选举;【" + currentIp + "】成为了TokenServer Master,端口:" + tokenServerPort));
                    apolloClusterConfigManager.changeMasterTokenServerAddress(currentIp, tokenServerPort);
                }

                @Override
                public void notLeader() {
                    log.warn("【" + name + "】失去了master");
                    //发送企业微信告警
                    dingerSender.send(MessageSubType.TEXT, DingerRequest.request("【" + name + "】失去了master"));
                }
            });
        }

        @Override
        public void close() throws IOException {
            leaderLatch.close(LeaderLatch.CloseMode.NOTIFY_LEADER);
        }

        public void start() {
            try {
                leaderLatch.start();
            } catch (Exception e) {
                log.error("leaderLatch start fail, e:", e);
            }
        }
    }
}
