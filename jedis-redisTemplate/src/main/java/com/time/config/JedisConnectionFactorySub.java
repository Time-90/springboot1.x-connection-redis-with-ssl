package com.time.config;

import com.time.prop.RedisProperties;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.util.Assert;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPoolConfig;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName JedisConnectionFactorySub.java
 * @Description 重写createCluster，JedisConnectionFactory原生的createCluster创建JedisCluster客户端时会出错
 * @createTime 2022年03月28日 16:01:00
 */
public class JedisConnectionFactorySub extends JedisConnectionFactory implements ApplicationContextAware {

    protected RedisProperties redisProperties;

    public JedisConnectionFactorySub(RedisClusterConfiguration redisClusterConfiguration, JedisPoolConfig jedisPoolConfig) {
        super(redisClusterConfiguration, jedisPoolConfig);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.redisProperties = (RedisProperties) applicationContext.getBean("redisProperties");
    }

    /**
     * 修复JedisConnectionFactory原生的createCluster创建JedisCluster客户端失败问题
     *
     * @param clusterConfig
     * @param poolConfig
     * @return
     */
    @Override
    protected JedisCluster createCluster(RedisClusterConfiguration clusterConfig, GenericObjectPoolConfig poolConfig) {
        Assert.notNull(clusterConfig, "Cluster configuration must not be null!");
        Set<HostAndPort> hostAndPort = new HashSet<HostAndPort>();
        for (RedisNode node : clusterConfig.getClusterNodes()) {
            hostAndPort.add(new HostAndPort(node.getHost(), node.getPort()));
        }
        return new JedisCluster(hostAndPort, redisProperties.getCluster().getConnectionTimeout(), redisProperties.getCluster().getSoTimeOut(), redisProperties.getCluster().getMaxAttempts(),
                                redisProperties.getCluster().getPassword(), redisProperties.getCluster().getClientName(), poolConfig, redisProperties.getCluster().isSsl());
    }
}
