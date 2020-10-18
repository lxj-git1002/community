package com.project.community.service;

import com.project.community.entity.User;

import java.util.Map;

public interface UserService  {
    //根据用户id查询用户的方法
    public User findUserById(int id);

    //返回注册的结果，需要的参数为一个user对象
    public Map<String,Object> register(User user);

    //登录
    public Map<String,Object> login(String username,String password,int expiredSeconds);

    //退出
    public void logOut(String ticket);
}
