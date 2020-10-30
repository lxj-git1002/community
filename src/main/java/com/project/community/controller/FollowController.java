package com.project.community.controller;

import com.project.community.annotation.LoginCheck;
import com.project.community.entity.User;
import com.project.community.service.FollowService;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    //只能登录后关注东西
    @LoginCheck
    //添加关注，用异步请求
    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int entityType,int entityId)
    {
        User user = hostHolder.getUser();
        followService.follow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"已关注");
    }

    //取消关注
    @LoginCheck
    @RequestMapping(path = "/unfollow",method = RequestMethod.POST)
    @ResponseBody
    public String unfollow(int entityType,int entityId)
    {
        User user = hostHolder.getUser();
        followService.unfollow(user.getId(),entityType,entityId);

        return CommunityUtil.getJsonString(0,"已取关");
    }

}
