package com.project.community.controller;

import com.project.community.entity.User;
import com.project.community.service.UserService;
import com.project.community.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private UserServiceImpl userService;

    //获取注册页面
    //访问方式
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage()
    {
        return "/site/register";
    }

    //注册请求
    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String Register(Model model, User user)
    {
        Map<String, Object> map = userService.register(user);
        if (map==null||map.isEmpty())
        {
            //注册成功
            model.addAttribute("msg","注册成功，快去邮箱激活吧😊");
            model.addAttribute("target","/index");
            //路由到指定的模板
            return "/site/operate-result";
        }
        else
        {
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("emailMsg",map.get("emailMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/register";
        }
    }
}
