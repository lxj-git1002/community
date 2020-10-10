package com.project.community.service.impl;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.entity.DiscussPost;
import com.project.community.service.DiscussPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class DiscussPostServiceImpl implements DiscussPostService {

    @Autowired
    private DiscussPostMapper discussPostMapper;

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
}
