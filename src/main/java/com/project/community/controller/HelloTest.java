package com.project.community.controller;

import com.project.community.util.CommunityUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/hello")
public class HelloTest {

    /*
     * 处理请求
     * ResponseBody表示服务器响应的是一个简单的数据
     * */

    @RequestMapping("/hello")
    @ResponseBody
    public String hello()
    {
        return "hello";
    }

    //请求路径 /test?cur=***&limit=***
    @RequestMapping(path = "test",method = RequestMethod.GET)
    @ResponseBody
    public String test(@RequestParam(name = "cur",required = false,defaultValue = "1") int cur,
                       @RequestParam(name = "limit",required = false,defaultValue = "10") int limit)
    {
        System.out.println(cur);
        System.out.println(limit);
        return "success";
    }

    //请求路径 /test/***
    @RequestMapping(path = "test1/{id}",method = RequestMethod.GET)
    @ResponseBody
    public String test2(@PathVariable(value = "id") int id)
    {
        System.out.println(id);
        return "success1";
    }

    //利用post提交数据
    @RequestMapping(path = "test2",method = RequestMethod.POST)
    @ResponseBody
    public String test3(String name,int age)
    {
        System.out.println(name);
        System.out.println(age);
        return "success2";
    }

    /*
     * 向浏览器返回响应数据
     * */

    //向浏览器响应动态的html
    @RequestMapping(path = "/test3",method = RequestMethod.GET)
    public ModelAndView get()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","aaaa");
        modelAndView.addObject("age",20);
        //将model数据传入的模板路径
        modelAndView.setViewName("/test/get");//这里的get就是get.html
        return modelAndView;
    }

    @RequestMapping(path = "/test4",method = RequestMethod.GET)
    public String get2(Model model)
    {
        model.addAttribute("name","bbbb");
        model.addAttribute("age",100);
        return "/test/get";//返回的是get.html的路径
    }

    //向浏览器相应json数据(异步请求（例如局部验证，比如注册的时候，输入昵称，需要严重是否已经被占用）一般相应json数据)
    //服务器将java对象响应给js。js要将java对象转成js对象。需要用到 java对象->json字符串->js对象
    @RequestMapping(value = "/test5",method = RequestMethod.GET)
    @ResponseBody//返回的响应如果是字符串必须加这个注解，json也是字符串所以要加
    public Map<String,Object> getJson()
    {
        Map<String, Object> res = new HashMap<>();
        res.put("aaa",1);
        res.put("bbb",2);
        return res;
    }

    //返回的是多组数据
    @RequestMapping(value = "/test6",method = RequestMethod.GET)
    @ResponseBody//返回的响应如果是字符串必须加这个注解，json也是字符串所以要加
    public List<Map<String,Object>> getJsons()
    {
        List<Map<String, Object>> res = new ArrayList<>();
        Map<String, Object> m1 = new HashMap<>();
        m1.put("aaa",1);
        m1.put("bbb",2);
        Map<String, Object> m2 = new HashMap<>();
        m2.put("ccc",1);
        m2.put("ddd",2);
        res.add(m1);
        res.add(m2);
        return res;
    }

    //cookies示例
    @RequestMapping(path = "/cookies/set",method = RequestMethod.GET)
    @ResponseBody
    public String setCookies(HttpServletResponse servletResponse)
    {
        //创建cookies对象
        Cookie cookie = new Cookie("code", CommunityUtil.generaterUUID());

        //设置生效范围（设置在哪些路径下才需要发送cookie）
        cookie.setPath("/community/hello");

        //cookie的生存时间（如果不设置，关闭浏览器cookie就会失效）
        cookie.setMaxAge(60*10);//10 minutes

        //发送cookie
        servletResponse.addCookie(cookie);

        return "set Cookies Success";
    }

    @RequestMapping(path = "/cookies/get",method = RequestMethod.GET)
    @ResponseBody
    public String getCookies(@CookieValue("code") String code)//将cookie中key为code的值赋值给code这个形参
    {
        System.out.println(code);
        return "get cookies success";
    }

    //session 示例
    @RequestMapping(path = "/session/set",method = RequestMethod.GET)
    @ResponseBody
    public String setSession(HttpSession session)//spring mvc 会自动创建session，并且注入进来
    //session可以存很多的数据，可以存任何类型的数据。
    {
        session.setAttribute("id",1);
        session.setAttribute("name","test");
        return "set session success";
    }
    @RequestMapping(path = "/session/get",method = RequestMethod.GET)
    @ResponseBody
    public String getSession(HttpSession session)
    {
        System.out.println(session.getAttribute("id"));
        System.out.println(session.getAttribute("name"));
        return "get session success";
    }

    //ajax 异步js和xml，不是刷新整个页面，而只是返回网页改变的部分
    //ajax例子
    @RequestMapping(path = "/ajax",method = RequestMethod.POST)
    //ajax实现异步请求，不向浏览器返回网页，只返回字符串。所以用responsebody注解
    @ResponseBody
    //post请求，浏览器向服务器提交数据，提交的数据写入参数中
    public String ajaxTest(String name,int age)
    {
        System.out.println(name);
        System.out.println(age);

        return CommunityUtil.getJsonString(0,"success");
    }

}