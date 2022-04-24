package com.time.controller;

import com.time.util.StringRedisTemplateJedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName StringRedisTemplateJedisTest.java
 * @Description 使用jedis客户端单节点/集群模式-StringRedisTemplate访问redis服务
 * @createTime 2022年03月28日 01:11:00
 */
@RestController
@RequestMapping("/jedis/string")
public class StringRedisTemplateJedisTest {

    @Autowired
    private StringRedisTemplateJedisUtil stringRedisTemplate;

    @GetMapping("/get/{key}")
    public String getKeyByLettuce(@PathVariable String key) {
        //根据key获取缓存中的val
        return stringRedisTemplate.get(key);
    }

    @GetMapping("/set/{key}/{value}")
    public String setKeyByLettuce(@PathVariable String key, @PathVariable String value) {
        stringRedisTemplate.set(key, value);
        return stringRedisTemplate.get(key);
    }
}
