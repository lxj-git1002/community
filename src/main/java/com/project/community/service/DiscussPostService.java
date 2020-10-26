package com.project.community.service;

import com.project.community.entity.DiscussPost;

import java.util.List;

//分页查询
public interface DiscussPostService {
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);
    public int findDiscussPostRows(int userId);

    //增加帖子
    public int addDiscussPost(DiscussPost post);

    //根据id查询
    public DiscussPost findDiscussPostById(int id);

    //更新帖子评论的数量
    public int updateCommentCount(int id,int commentCount);
}
