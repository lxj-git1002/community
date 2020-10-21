package com.project.community.controller;

import com.google.code.kaptcha.Producer;
import com.project.community.dao.UserMapper;
import com.project.community.entity.User;
import com.project.community.service.impl.UserServiceImpl;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.EmailCheck;
import com.project.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    //生成验证码
    private Producer kaptchaProducer;

    @Autowired
    private MailClient mailClient;

    @Autowired
    private UserServiceImpl userService;

    @Value("${server.servlet.context-path}")
    private String contextPath;

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


    //生成验证码
    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)
    //通过reponse对象向浏览器输出验证码图片。
    //将验证码图片保存在session中，下次登录的时候进行验证
    {
        //生成验证码
        String text = kaptchaProducer.createText();
        //根据text生成图片
        BufferedImage image = kaptchaProducer.createImage(text);

        //将验证码文字存入到session中为了后续的使用
        session.setAttribute("kaptcha",text);

        //将图片输出给浏览器
        response.setContentType("image/png");//声明给浏览器返回的是声明类型的文件
        try {
            ServletOutputStream stream = response.getOutputStream();
            ImageIO.write(image,"png",stream);
        }
        catch (Exception e)
        {
            LOGGER.error("响应验证码失败",e.getMessage());
        }
    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String kaptcha, boolean remember,//页面传入的数据
                        Model model,//返回数据时候需要响应页面，声明一个model
                        HttpSession session,//生成验证码时，验证码存在session中，登录时需要用到进行比较
                        HttpServletResponse response)//登录成功需要将ticket发给客户端，让客户端进行保存，则需要用到cookie
    {
        //判断验证码
        String code = ((String) session.getAttribute("kaptcha"));
        if (StringUtils.isBlank(code)||StringUtils.isBlank(kaptcha)|| !code.equalsIgnoreCase(kaptcha))//或略大小写
        {
            model.addAttribute("codeMsg","验证码错误");
            return "/site/login";
        }

        //验证账号密码
        int expiredSeconds = remember?REMEMBER_EXPIRED_SECONDS:DEFAULT_EXPIRED_SECONDS;
        Map<String, Object> map = userService.login(username, password, expiredSeconds);

        //登录成功
        if (map.containsKey("ticket"))
        {
            //登录成功则先取出ticket，通过cookie将ticket发送给客户端
            //定义cookie
            Cookie cookie = new Cookie("ticket",map.get("ticket").toString());
            cookie.setPath(contextPath);
            cookie.setMaxAge(expiredSeconds);

            //将cookie发送给页面客户端
            response.addCookie(cookie);

            //登录成功则重定向到首页
            return "redirect:/index";
        }
        else
        {
            //登录失败则将错误信息通过model给客户端
            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));
            return "/site/login";
        }
    }


    @RequestMapping(path = "/logout",method = RequestMethod.GET)
    public String logout(@CookieValue("ticket") String ticket)
    {
        userService.logOut(ticket);
        return "redirect:/index";//退出后重定向到首页
    }

    //访问忘记密码页面
    @RequestMapping(path = "/forget",method = RequestMethod.GET)
    public String forgetPage()
    {
        return "/site/forget";
    }

    @RequestMapping(path = "/forget",method = RequestMethod.POST)
    public String forget(String myEmail,String newPwd,String code,Model model,HttpSession session)
    {

        User user = userService.selectByemail(myEmail);

        //从session中获得验证码
        String ver = (String) session.getAttribute("ver");

        if (code==null||ver==null||!code.equals(ver))
        {
            model.addAttribute("error","验证码错误");
            return "/site/forget";
        }

        if (newPwd==null||newPwd.length()<4)
        {
            model.addAttribute("error","密码太短");
            return "/site/forget";
        }

        //验证码正确,更新数据库密码，然后返回到登录界面
        userService.updatePwd(user.getId(),CommunityUtil.MD5(newPwd+user.getSalt()));

        //重新登陆
        model.addAttribute("msg","密码找回，请重新登陆");
        model.addAttribute("target","/login");
        return "/site/operate-result";
    }

    //点击获取验证码，后得到user的邮箱。然后通过这个邮箱发送验证码
    @RequestMapping(path = "/verification")
    @ResponseBody
    public String Verification(@RequestParam(name = "mail") String mail,HttpSession session,Model model)
    {
        if (StringUtils.isBlank(mail)|| !EmailCheck.isEmail(mail))
        {
            return "fail";
        }

        //检查邮箱是否已经注册
        User user = userService.selectByemail(mail);

        if (user==null)
        {
            return "fail";
        }

        //生成一个验证码
        String s = CommunityUtil.generaterUUID().substring(0,6);
        session.setAttribute("ver",s);
        //发送邮件
        mailClient.sendMail(mail,"重置密码验证码",s);
        return "succ";
    }

}
