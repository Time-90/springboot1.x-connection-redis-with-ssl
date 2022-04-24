package com.time.config;

import com.time.prop.RedisProperties;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedisConfig.java
 * @Description
 * @createTime 2022年01月14日 18:47:00
 */
public class RedisConfig {
    @Autowired
    protected RedisProperties redisProperties;
}
