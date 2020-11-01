package com.project.community.service;

import java.util.List;
import java.util.Map;

public interface FollowService {

    //关注
    public void follow(int userId,int entityType,int entityId);

    //取消关注
    public void unfollow(int userId,int entityType,int entityId);

    //查询关注的数量
    public long findFolloweeNum(int userId,int entityType);

    //查询被关注的数量
    public long findFollowerNum(int entityType,int entityId);

    //当前用户是否关注了当前目标状态的查询
    public boolean hasFollow(int userId,int entityType,int entityId);

    //查询用户关注的人
    public List<Map<String,Object>> findFollowees(int userId,int offset,int limit);

    //查询某个用户的粉丝
    public List<Map<String,Object>> findFollowers(int userId,int offset,int limit);

}
