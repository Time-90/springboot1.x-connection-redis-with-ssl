package com.time.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.time.prop.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

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
     * !!!注意这里不能直接使用JedisConnectionFactory工厂创建jedis链接，因为2.x和3.x代码有些差异，会导致JedisConnectionFactory构建失败，
     * 如果访问单节点redis服务，则可以正常使用jedis2.x版本利用JedisConnectionFactory创建链接，访问集群则不行，集群必须要使用jedis3.x
     * <p>
     * 除了util包的位置变化之外，3.x版本对于Protocol类中的内部枚举Command实现了redis.clients.jedis.commands.ProtocolCommand接口，导致创建JedisCluster时失败
     * 如果要使用JedisConnectionFactory需要两步操作：
     * 1、需要修改源码降级部分功能，这里可使用resources/lib下我已打包好的jedis-3.3.1（非官方版本，主要对Spring Data Redis1.8.9作了适配）
     * 2、新建JedisConnectionFactorySub类继承JedisConnectionFactory，并重写createCluster方法，修复集群模式下无法创建JedisCluster
     *
     * @return
     */
    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        log.info("creating JedisConnectionFactory");
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
        jedisPoolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        jedisPoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        jedisPoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());

        JedisConnectionFactory jedisConnectionFactory;
        if (redisProperties.getCluster().isClusterEnable()) {
            log.info("creating cluster JedisConnectionFactory");
            //创建redis集群连接工厂
            jedisConnectionFactory = new JedisConnectionFactorySub(new RedisClusterConfiguration(redisProperties.getCluster().getNodes()), jedisPoolConfig);
        } else {
            log.info("creating single JedisConnectionFactory");
            //创建redis单机版连接工厂
            String node = redisProperties.getCluster().getNodes().get(0);
            String[] split = node.split(":");
            jedisConnectionFactory = new JedisConnectionFactory(jedisPoolConfig);
            jedisConnectionFactory.setHostName(split[0]);
            jedisConnectionFactory.setPort(Integer.valueOf(split[1]));
        }

        jedisConnectionFactory.setPassword(redisProperties.getCluster().getPassword());
        jedisConnectionFactory.setClientName(redisProperties.getCluster().getClientName());
        jedisConnectionFactory.setUseSsl(redisProperties.getCluster().isSsl());
        log.info("creating JedisConnectionFactory success {}", jedisConnectionFactory);
        return jedisConnectionFactory;
    }

    /**
     * 注册StringRedisTemplate
     * key 和 value 都为String类型
     * 都使用Jackson2JsonRedisSerializer进行序列化
     */
    @Bean
    public StringRedisTemplate stringRedisTemplateJedis(JedisConnectionFactory jedisConnectionFactory) {
        log.info("creating stringRedisTemplateJedis bean by Jedis:[{}]", jedisConnectionFactory);
        return new StringRedisTemplate(jedisConnectionFactory);
    }

    /**
     * 注册RedisTemplate
     * key 为String类型
     * value 为 Object 类型
     * 都使用Jackson2JsonRedisSerializer进行序列化
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplateJedis(JedisConnectionFactory jedisConnectionFactory) {
        log.info("creating redisTemplateJedis bean by Jedis:[{}]", jedisConnectionFactory);
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }
}
