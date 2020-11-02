package com.project.community.dao;

import com.project.community.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CommentMapper {

    //查询评论总数
    int selectCountByEntity(int entityType,int entityId);

    //评论很多所以要用到分页
    List<Comment> selectCommentByEntity(int entityType,int entityId,int offset,int limit);

    //添加评论
    int insertComment(Comment comment);

    //通过评论的查询发布评论的人
    Comment selectCommentById(int id);

}
