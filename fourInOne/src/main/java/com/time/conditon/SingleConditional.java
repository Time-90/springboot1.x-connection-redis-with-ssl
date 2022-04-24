package com.time.conditon;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedisConfig.java
 * @Description 单节点标识类
 * @createTime 2022年03月25日 22:47:00
 */
@Slf4j
public class SingleConditional implements Condition {

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        Environment environment = conditionContext.getEnvironment();
        return !Boolean.parseBoolean(environment.getProperty("spring.redis.cluster.clusterEnable"));
    }
}
