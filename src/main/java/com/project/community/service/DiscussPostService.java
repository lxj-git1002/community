package com.project.community.service;

import com.project.community.entity.DiscussPost;

import java.util.List;

//分页查询
public interface DiscussPostService {
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);
    public int findDiscussPostRows(int userId);
}
