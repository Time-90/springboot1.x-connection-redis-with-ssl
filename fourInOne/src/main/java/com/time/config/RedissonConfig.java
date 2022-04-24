package com.time.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedissonConfig.java
 * @Description Redisson配置类
 * @createTime 2022年03月26日 17:14:00
 */
@Slf4j
@Configuration
public class RedissonConfig extends RedisConfig {

    private final static String COLLECTION_PREFIX = "redis://";
    private final static String COLLECTION_SSL_PREFIX = "rediss://";

    /**
     * 注册RedissonClient
     * config.setTransportMode(TransportMode.EPOLL);
     *
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonClient() {
        log.info("creating redissonClient");
        // 1、创建配置
        Config config = new Config();

        // 2、配置模式
        if (redisProperties.getCluster().isClusterEnable()) {
            log.info("creating redissonClientCluster");
            ClusterServersConfig cluster = config.useClusterServers();
            cluster.setMasterConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
            cluster.setConnectTimeout(redisProperties.getCluster().getConnectionTimeout());
            cluster.setClientName(redisProperties.getCluster().getClientName()).setPassword(redisProperties.getCluster().getPassword());
            List<String> nodes = redisProperties.getCluster().getNodes();
            nodes.forEach(item -> cluster.addNodeAddress((redisProperties.getCluster().isSsl() ? COLLECTION_SSL_PREFIX : COLLECTION_PREFIX) + item));
        } else {
            log.info("creating redissonClient");
            SingleServerConfig single = config.useSingleServer();
            single.setConnectionMinimumIdleSize(redisProperties.getPool().getMaxIdle());
            single.setConnectTimeout(redisProperties.getCluster().getConnectionTimeout());
            single.setClientName(redisProperties.getCluster().getClientName()).setPassword(redisProperties.getCluster().getPassword());
            single.setAddress(redisProperties.getCluster().isSsl() ? COLLECTION_SSL_PREFIX : COLLECTION_PREFIX + redisProperties.getCluster().getNodes().get(0));
        }
        log.info("creating redissonClient success");
        return Redisson.create(config);
    }
}
