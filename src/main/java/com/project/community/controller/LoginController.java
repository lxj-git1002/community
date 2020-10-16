package com.project.community.controller;

import com.project.community.entity.User;
import com.project.community.service.impl.UserServiceImpl;
import com.project.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserServiceImpl userService;

    //获取注册页面
    //访问方式
    @RequestMapping(path = "/register",method = RequestMethod.GET)
    public String getRegisterPage()
    {
        return "/site/register";
    }

    //访问登录页面
    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage()
    {
        return "/site/login";
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

    //处理激活请求,请求路径为 url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
    @RequestMapping(path = "/activation/{userId}/{code}" ,method = RequestMethod.GET)
    public String activation(Model model, @PathVariable("userId") int userId , @PathVariable("code") String code)
    {
        int res = userService.activation(userId, code);
        if (res==ACTIVATION_SUCCESS)
        {
            model.addAttribute("msg","激活成功，快去登录吧😊");
            model.addAttribute("target","/login");
        }
        else if (res==ACTIVATION_REPEAT)
        {
            model.addAttribute("msg","不要重复激活🙅‍");
            model.addAttribute("target","/index");
        }
        else
        {
            model.addAttribute("msg","激活码不正确❌‍");
            model.addAttribute("target","/index");
        }
        return "/site/operate-result";
    }
}
