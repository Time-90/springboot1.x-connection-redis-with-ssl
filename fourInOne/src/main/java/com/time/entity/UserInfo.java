package com.time.entity;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yonghao.tang
 * @version 1.0.0
 * @ClassName UserInfo.java
 * @Description TODO
 * @createTime 2022年03月26日 20:28:00
 */
@Data
public class UserInfo implements Serializable {
    private Integer id;
    private String username;
    private Integer age;
    private String email;
    private String password;
    private Integer sex;
    private String phone;
}
