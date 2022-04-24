package com.time.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lambdaworks.redis.RedisClient;
import com.time.prop.RedisProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.DefaultLettucePool;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedisConfig.java
 * @Description 注册RedisTemplate
 * @createTime 2021年12月20日 10:02:43
 */
@Slf4j
@Configuration
@ConditionalOnClass(RedisClient.class)
public class RedisConfig {
    @Autowired
    protected RedisProperties redisProperties;

    /**
     * 注册LettuceConnectionFactory
     * 平台在用的springboot版本为1.5.9,自动依赖的spring Data Redis版本为1.8.9.RELEASE，因此只能使用4.5.X及以下版本的lettuce，不支持新版本
     * <dependency>
     * <groupId>biz.paluch.redis</groupId>
     * <artifactId>lettuce</artifactId>
     * <version>4.5.0.Final</version>
     * </dependency>
     * <p>
     * 4.5.x版本及以下 groupId：biz.paluch.redis
     * 5.x版本及以下 groupId：io.lettuce
     *
     * @return
     */
    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        log.info("creating LettuceConnectionFactory");

        /**
         * Spring Data Redis1.8.9 + lettuce 4.5.0.Final暂时不能使用连接池
         */
        GenericObjectPoolConfig lettucePoolConfig = new GenericObjectPoolConfig();
        lettucePoolConfig.setMaxWaitMillis(redisProperties.getPool().getMaxWait());
        lettucePoolConfig.setMaxTotal(redisProperties.getPool().getMaxActive());
        lettucePoolConfig.setMaxIdle(redisProperties.getPool().getMaxIdle());
        lettucePoolConfig.setMinIdle(redisProperties.getPool().getMinIdle());
        DefaultLettucePool lettucePool = new DefaultLettucePool();
        lettucePool.setPoolConfig(lettucePoolConfig);

        LettuceConnectionFactory lettuceConnectionFactory;

        if (redisProperties.getCluster().isClusterEnable()) {
            log.info("creating cluster LettuceConnectionFactory");
            //创建redis集群连接工厂
            lettuceConnectionFactory = new LettuceConnectionFactory(new RedisClusterConfiguration(redisProperties.getCluster().getNodes()));
        } else {
            log.info("creating single LettuceConnectionFactory");
            //创建redis单机版连接工厂
            String node = redisProperties.getCluster().getNodes().get(0);
            String[] split = node.split(":");
            lettuceConnectionFactory = new LettuceConnectionFactory();
            lettuceConnectionFactory.setHostName(split[0]);
            lettuceConnectionFactory.setPort(Integer.valueOf(split[1]));
        }

        lettuceConnectionFactory.setPassword(redisProperties.getCluster().getPassword());
        lettuceConnectionFactory.setUseSsl(redisProperties.getCluster().isSsl());
        log.info("creating LettuceConnectionFactory success {}", lettuceConnectionFactory);
        return lettuceConnectionFactory;
    }


    /**
     * 注册StringRedisTemplate
     *
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    public StringRedisTemplate stringRedisTemplateLettuce(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("creating stringRedisTemplateLettuce bean by Lettuce:[{}]", lettuceConnectionFactory);
        StringRedisTemplate template = new StringRedisTemplate();
        //注入Lettuce连接工厂
        template.setConnectionFactory(lettuceConnectionFactory);
        return template;
    }

    /**
     * 注册RedisTemplate
     *
     * @param lettuceConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplateLettuce(LettuceConnectionFactory lettuceConnectionFactory) {
        log.info("creating redisTemplateLettuce bean by Lettuce:[{}]", lettuceConnectionFactory);
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(lettuceConnectionFactory);
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

