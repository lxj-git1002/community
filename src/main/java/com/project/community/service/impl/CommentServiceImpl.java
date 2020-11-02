package com.project.community.service.impl;

import com.project.community.dao.CommentMapper;
import com.project.community.entity.Comment;
import com.project.community.service.CommentService;
import com.project.community.service.DiscussPostService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.SensitiveFilter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService, CommunityConstant {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private DiscussPostService discussPostService;

    @Override
    public List<Comment> findCommentByEntity(int entityType, int entityId, int offset, int limit) {
        return commentMapper.selectCommentByEntity(entityType,entityId,offset,limit);
    }

    @Override
    public int findCommentCount(int entityType, int entityId) {
        return commentMapper.selectCountByEntity(entityType,entityId);
    }

    //当前函数添加评论，在两个事务中，需要对comment表处理，同时处理后需要对discusspost表进行更新，则需要进行事务隔离
    @Override
    //Transactional 是通过Spring 来实现事务隔离
    @Transactional(isolation = Isolation.READ_COMMITTED,propagation = Propagation.REQUIRED)
    public int addComment(Comment comment) {

        if (comment==null|| StringUtils.isBlank(comment.getContent()))
        {
            throw new IllegalArgumentException("评论不能为空");
        }

        //对comment进行过滤敏感词过滤，html过滤
        comment.setContent(HtmlUtils.htmlEscape(comment.getContent()));
        comment.setContent(sensitiveFilter.filter(comment.getContent()));

        //存入数据库
        int i = commentMapper.insertComment(comment);

        //更新评论数量
        //如果当前评论是对帖子的评论则更新评论数量
        if (comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //得到帖子当前的评论数量
            int count = commentMapper.selectCountByEntity(comment.getEntityType(), comment.getEntityId());
            discussPostService.updateCommentCount(comment.getEntityId(),count);
        }
        return i;
    }

    @Override
    public Comment findCommentById(int id) {
        return commentMapper.selectCommentById(id);
    }
}
