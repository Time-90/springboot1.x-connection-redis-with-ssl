package com.time.controller;

import com.time.util.IJedisTemplate;
import com.time.util.JedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName JedisTemplateController.java
 * @Description 使用jedisTemplate访问redis服务
 * @createTime 2022年03月26日 16:23:00
 */
@RestController
@RequestMapping("/jedisTemplate")
public class JedisTemplateController {

    @Autowired
    IJedisTemplate jedisTemplate;

    /**
     * @param key
     * @return
     */
    @GetMapping("/get/{key}")
    public String getKey(@PathVariable String key) {
        return jedisTemplate.get(key);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @GetMapping("/set/{key}/{value}")
    public String setKey(@PathVariable String key, @PathVariable String value) {
        jedisTemplate.set(key, value);
        return jedisTemplate.get(key);
    }
}
