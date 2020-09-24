package com.project.community.entity;

public class User {

    private int id;
    private String username;
    private String password;
    private String salt;//添加一个随机字符串来增强密码的安全性
    private String email;
    private int type;

}
