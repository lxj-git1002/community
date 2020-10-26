package com.project.community.controller;

import com.project.community.entity.Comment;
import com.project.community.service.CommentService;
import com.project.community.util.HostHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private HostHolder hostHolder;//评论的时候需要是登录状态，知道当前的登录的人的id

    @RequestMapping(path = "/add/{discusspostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discusspostId") int discusspostId, Comment comment)
    {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //评论结束后重定向到帖子详情页面
        return "redirect:/discuss/detail/"+discusspostId;
    }
}
