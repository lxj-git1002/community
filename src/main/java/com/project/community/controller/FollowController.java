package com.project.community.controller;

import com.project.community.annotation.LoginCheck;
import com.project.community.entity.Page;
import com.project.community.entity.User;
import com.project.community.service.FollowService;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Controller
public class FollowController implements CommunityConstant {

    @Autowired
    private FollowService followService;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private UserService userService;

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

    //查询当前userid 关注的人
    @RequestMapping(path = "/followees/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId")int userId, Page page, Model model)
    {

        //得到user
        User user = userService.findUserById(userId);
        if (user==null)
        {
            throw new RuntimeException("用户不存在");
        }

        //将user传给页面 ，用于在显示页的最上面显示 user关注的人
        model.addAttribute("user",user);

        //对分页数据进行设置
        page.setLimit(2);
        page.setPath("/followees"+userId);
        page.setRows(((int) followService.findFolloweeNum(userId, ENTITY_TYPE_USER)));

        List<Map<String, Object>> list = followService.findFollowees(userId, page.getOffset(), page.getLimit());

        //判断当前user是否对userid对应的list中的对象进行了关注，如果没有关注则会显示关注按钮
        //当前的user是值登录的user，userid对应的user是查看的user
        if (list!=null)
        {
            for (Map<String, Object> map : list) {
                User u = ((User) map.get("user"));
                boolean followed = hasFollowed(u.getId());
                map.put("hasFollowed",followed);
            }
        }

        //将list的结果通过model传给模版
        model.addAttribute("users",list);

        //返回模版
        return "/site/followee";
    }

    //查询当前用户userid的粉丝数量
    @RequestMapping(path = "/followers/{userId}",method = RequestMethod.GET)
    public String getFollowees(@PathVariable("userId") int userId,Model model,Page page)
    {
        //得到当前的userid对应的user
        User user = userService.findUserById(userId);
        if (user==null)
        {
            throw new RuntimeException("用户不存在");
        }

        //将user传到模版，用于在页面的顶部显示
        model.addAttribute("user",user);

        //对分页数据进行设置
        page.setLimit(2);
        page.setPath("/followees"+userId);
        page.setRows(((int) followService.findFollowerNum(ENTITY_TYPE_USER, userId)));

        List<Map<String, Object>> list = followService.findFollowers(user.getId(), page.getOffset(), page.getLimit());

        if (list!=null)
        {
            //对list进行遍历，查询里面每一个粉丝，看当前的user是否关注了当前userid对应的user的粉丝
            //当前的user是值登录的user，userid对应的user是查看的user
            for (Map<String, Object> map : list) {
                User u = ((User) map.get("user"));
                //查询关注状态
                boolean hasFollowed = hasFollowed(u.getId());
                map.put("hasFollowed",hasFollowed);
            }
        }

        model.addAttribute("users",list);
        return "/site/follower";
    }


    private boolean hasFollowed(int userId)
    {
        User user = hostHolder.getUser();
        if (user==null)
            return false;
        return followService.hasFollow(user.getId(),ENTITY_TYPE_USER,userId);
    }

}
