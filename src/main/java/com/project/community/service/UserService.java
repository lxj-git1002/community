package com.project.community.service;

import com.project.community.entity.LoginTicket;
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

    //查询loginticket对象
    public LoginTicket findLoginTicket(String ticket);

    //更新用户的头像
    public int updateHeader(int userId, String headerUrl);

    //更新用户的密码
    public int updatePwd(int userId,String newPwd);

    //通过email查询用户
    public User selectByemail(String email);
}
