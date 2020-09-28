package com.project.community.service;

import com.project.community.entity.User;

public interface UserService {
    //根据用户id查询用户的方法
    public User findUserById(int id);
}
