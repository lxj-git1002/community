package com.project.community.controller;

import com.project.community.entity.Comment;
import com.project.community.entity.DiscussPost;
import com.project.community.entity.Event;
import com.project.community.event.EventProducer;
import com.project.community.service.CommentService;
import com.project.community.service.DiscussPostService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    private CommentService commentService;

    //用于产生事件
    @Autowired
    private EventProducer producer;

    @Autowired
    private HostHolder hostHolder;//评论的时候需要是登录状态，知道当前的登录的人的id

    @Autowired
    private DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{discusspostId}",method = RequestMethod.POST)
    public String addComment(@PathVariable("discusspostId") int discusspostId, Comment comment)
    {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);

        //评论之后，系统发送系统通知
        Event event = new Event()
                .setTopic(TOPIC_COMMENT)
                .setUserId(hostHolder.getUser().getId())
                .setEntityType(comment.getEntityType())
                .setEntityId(comment.getEntityId())
                .setData("postId",discusspostId);

        if (comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //如果评论的是帖子，则通过帖子id查询，发布帖子的作者的id
            DiscussPost target = discussPostService.findDiscussPostById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }
        else if (comment.getEntityType()==ENTITY_TYPE_COMMENT)
        {
            //如果评论的是评论
            Comment target = commentService.findCommentById(comment.getEntityId());
            event.setEntityUserId(target.getUserId());
        }

        //发布系统消息
        producer.fireEvent(event);

        //评论结束后重定向到帖子详情页面
        return "redirect:/discuss/detail/"+discusspostId;
    }
}
