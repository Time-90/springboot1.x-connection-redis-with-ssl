package com.time.controller;

import com.time.util.StringRedisTemplateLettuceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName StringRedisTemplateLettuceController.java
 * @Description 使用lettuce客户端单节点/集群模式-StringRedisTemplateTest访问redis服务
 * @createTime 2022年01月14日 19:23:00
 */
@RestController
@RequestMapping("/lettuce/string")
public class StringRedisTemplateLettuceController {

    @Autowired
    private StringRedisTemplateLettuceUtil stringRedisTemplate;

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
