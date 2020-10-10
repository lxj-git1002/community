package com.project.community.service;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


public interface DiscussPostService {
    public List<DiscussPost> findDiscussPosts(int userId,int offset,int limit);
    public int findDiscussPostRows(int userId);
}
