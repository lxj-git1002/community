package com.project.community.service.impl;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.entity.DiscussPost;
import com.project.community.service.DiscussPostService;
import com.project.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.List;

@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Override
    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit) {
       return discussPostMapper.selectDiscussPosts(userId,offset,limit);
       //这里查询到的discussPost中的user是以userid显示，
        // 要调用findUserById函数将userid和username进行转换
    }

    @Override
    public int findDiscussPostRows(int userId) {
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    @Override
    public int addDiscussPost(DiscussPost post) {
        //判断参数
        if (post==null)
            throw new IllegalArgumentException("帖子不能为空");

        //对post中的数据进行敏感词过滤
        //post中的title， content，tag进行敏感词过滤
        //处理敏感词之前先对html进行转义
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));
        post.setTag(HtmlUtils.htmlEscape(post.getTag()));

        //过滤
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));
        post.setTag(sensitiveFilter.filter(post.getTag()));

        //数据处理完成后进行数据的插入
        return discussPostMapper.insertDiscussPost(post);
    }

    @Override
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    @Override
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id,commentCount);
    }
}
