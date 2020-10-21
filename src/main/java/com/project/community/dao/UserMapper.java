package com.project.community.dao;

import com.project.community.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {

    //根据id查询
    User selectById(int id);

    //根据name查询
    User selectByName(String username);

    //根据email查询
    User selectByEmail(String email);

    //添加用户
    int insertUser (User user);

    //修改用户的status,根据id将原来的status修改为新的status，这里传入的是新的status
    int updateStatus(int id,int status);

    //修改用户的status,根据id将原来的headerUrl修改为新的headerUrl，这里传入的是新的headerUrl
    int updateHeader(int id,String headerUrl);

    //修改用户的status,根据id将原来的Password修改为新的Password，这里传入的是新的Password
    int updatePassword(int id,String password);
}
