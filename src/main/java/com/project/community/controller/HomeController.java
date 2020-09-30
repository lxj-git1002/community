package com.project.community.controller;

import com.project.community.entity.DiscussPost;
import com.project.community.entity.User;
import com.project.community.service.DiscussPostService;
import com.project.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    //显示查询到的帖子，并且需要将帖子数据库中的userid转成username，所以也需要用户表的操作
    @Autowired
    private DiscussPostService discussPostService;
    @Autowired
    private UserService userService;

    @RequestMapping(path = "/index",method = RequestMethod.GET)//访问首页，并且访问方式为get
    public String getIndexPage(Model model)//返回的是视图的名称
    {
        List<DiscussPost> posts = discussPostService.findDiscussPosts(0, 0, 3);
        List<Map<String, Object>> discussPosts = new ArrayList<>();
        if (posts != null) {
            for (DiscussPost post : posts) {
                Map<String, Object> map = new HashMap<>();
                map.put("post", post);
                User user = userService.findUserById(post.getUserId());
                map.put("user", user);
                discussPosts.add(map);
            }
        }
        model.addAttribute("discussPosts", discussPosts);
        return "/index";
    }
}
