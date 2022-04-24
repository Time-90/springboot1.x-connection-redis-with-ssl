package com.time.config;

import com.time.conditon.ClusterConditional;
import com.time.conditon.SingleConditional;
import com.time.prop.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedisConfig.java
 * @Description 注册jedisCluster客户端
 * @createTime 2021年12月20日 10:02:43
 */
@Slf4j
@Configuration
public class RedisConfig {
    @Autowired
    protected RedisProperties redisProperties;

    /**
     * 注册Jedis bean
     * Jedis(final String host, final int port, final int timeout, final boolean ssl)
     */
    @Bean
    @Conditional({SingleConditional.class})
    public Jedis jedis() {
        log.info("creating Single jedis bean");
        String node = redisProperties.getCluster().getNodes().get(0);
        String[] split = node.split(":");
        Jedis jedis = new Jedis(split[0], Integer.valueOf(split[1]), redisProperties.getCluster().getConnectionTimeout(), redisProperties.getCluster().isSsl());
        jedis.auth(redisProperties.getCluster().getPassword());
        log.info("creating Single jedis success");
        return jedis;
    }

    /**
     * 注册JedisCluster bean
     */
    @Bean
    @Conditional({ClusterConditional.class})
    public JedisCluster jedisCluster() {
        log.info("creating jedisCluster bean");
        /**
         * 配置连接池
         */
        GenericObjectPoolConfig jedisPoolConfig = new GenericObjectPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
        jedisPoolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        jedisPoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());

        /**
         * 配置客户端
         */
        Set<HostAndPort> jedisClusterNode = new HashSet<>();
        for (String node : redisProperties.getCluster().getNodes()) {
            String[] split = node.split(":");
            jedisClusterNode.add(new HostAndPort(split[0], Integer.valueOf(split[1])));
        }

        /**
         * jedis客户端3.x版本之前不支持集群SSL链接，连接AWS redis加密资源需要开启SSL，需要换成3.x版本以上
         * JedisCluster(
         *      HostAndPort node,
         *      int connectionTimeout,
         *      int soTimeout,
         *      int maxAttempts,
         *      String password,
         *      String clientName,
         *      final GenericObjectPoolConfig poolConfig,
         *      boolean ssl
         *      )
         */
        JedisCluster jedisCluster = new JedisCluster(jedisClusterNode, redisProperties.getCluster().getConnectionTimeout(), redisProperties.getCluster().getSoTimeOut(),
                                                     redisProperties.getCluster().getMaxAttempts(), redisProperties.getCluster().getPassword(), redisProperties.getCluster().getClientName(),
                                                     jedisPoolConfig, redisProperties.getCluster().isSsl());
        log.info("creating JedisCluster bean success");
        return jedisCluster;
    }
}
