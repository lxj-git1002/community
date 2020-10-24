package com.project.community.service;

import com.project.community.entity.Comment;

import java.util.List;

public interface CommentService {

    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit);

    public int findCommentCount(int entityType,int entityId);

    //增加评论
    public int addComment(Comment comment);
}
