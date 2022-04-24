package com.time.controller;

import com.time.entity.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName RedissonTest.java
 * @Description 使用Redisson客户端访问redis服务
 * @createTime 2022年03月26日 17:17:00
 */
@Slf4j
@RestController
@RequestMapping("/redisson")
public class RedissonTest {

    @Autowired
    RedissonClient redisson;

    private List<UserInfo> userlist;

    {
        userlist = generateUser();
    }

    /**
     * 测试RMap,put方法的时候就会同步到redis中 ,在redis中创建了一张userMap表
     *
     * @return
     */
    @GetMapping("/map/set")
    public RMap<Object, UserInfo> mapSet() {
        RMap<Integer, UserInfo> map = redisson.getMap("userMap");
        userlist.forEach(u -> map.put(u.getId(), u));
        return redisson.getMap("userMap");
    }

    /**
     * 测试RMap,put方法的时候就会同步到redis中 ,在redis中创建了一张userMap表
     *
     * @param id
     * @return
     */
    @GetMapping("/map/get/{id}")
    public UserInfo mapGet(@PathVariable Integer id) {
        RMap<Integer, UserInfo> map = redisson.getMap("userMap");
        return map.get(id);
    }

    /**
     * 测试Set集合 ,创建userSet的set集合表
     *
     * @return
     */
    @GetMapping("/set/test")
    public Set<UserInfo> setTest() {
        Set<UserInfo> userSet = redisson.getSet("userSet");
        userlist.forEach(u -> userSet.add(u));
        return redisson.getSet("userSet");

    }

    /**
     * 测试Queue队列
     *
     * @return
     */
    @GetMapping("/queue/test")
    public Queue<UserInfo> queueTest() {
        Queue<UserInfo> userQueue = redisson.getQueue("userQueue");
        userlist.forEach(u -> userQueue.add(u));
        return redisson.getQueue("userQueue");
    }


    /**
     * 生成测试用户数据
     *
     * @return
     */
    private List<UserInfo> generateUser() {
        List<UserInfo> userlist = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            UserInfo user = new UserInfo();
            user.setId(1000 + i);
            user.setUsername("tang" + i);
            user.setAge(20 + i);
            user.setPassword("123456");
            user.setEmail("test" + i + "@126.com");
            user.setSex(i % 2 == 0 ? 1 : 2);
            user.setPhone("1888888888" + i);
            userlist.add(user);
        }
        return userlist;
    }
}
