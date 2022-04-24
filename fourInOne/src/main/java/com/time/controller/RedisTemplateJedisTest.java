package com.time.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.time.util.RedisTemplateJedisUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedisTemplateJedisTest.java
 * @Description 使用jedis客户端单节点/集群模式-redisTemplate访问redis服务
 * @createTime 2022年03月29日 01:08:00
 */
@RestController
@RequestMapping("/jedis/redisTemplate")
public class RedisTemplateJedisTest {

    @Autowired
    private RedisTemplateJedisUtil redisTemplate;

    /**
     * @param key
     * @return
     */
    @GetMapping("/get/{key}")
    public String getKey(@PathVariable String key) {
        return (String) redisTemplate.get(key);
    }

    /**
     * @param key
     * @param value
     * @return
     */
    @GetMapping("/set/{key}/{value}")
    public String setKey(@PathVariable String key, @PathVariable String value) {
        redisTemplate.set(key, value);
        return (String) redisTemplate.get(key);
    }

    /**
     * 获取对象
     *
     * @return
     */
    @GetMapping("/getObj")
    public Map<Object, Object> getObject() {
        return redisTemplate.hmget("obj2");
    }

    /**
     * 保存对象
     *
     * @return
     */
    @GetMapping("/setObj")
    public Map<Object, Object> setObject() {
        HashMap<String, Object> objmap = new HashMap<>(10);
        objmap.put("key1", "value1");
        objmap.put("key2", "value2");
        objmap.put("key3", "value3");
        objmap.put("key4", "value4");
        objmap.put("key5", "value5");
        redisTemplate.hmset("obj2", objmap, 3600);
        return redisTemplate.hmget("obj2");
    }
}
