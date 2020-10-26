package com.project.community.service.impl;

import com.project.community.dao.LoginTicketMapper;
import com.project.community.dao.UserMapper;
import com.project.community.entity.LoginTicket;
import com.project.community.entity.User;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService , CommunityConstant {

    //注入邮件客户端
    @Autowired
    private MailClient mailClient;

    //注入模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    //注册的时候发邮件要生成一个激活码，激活码中要包含域名以及项目名。
    //注入
    @Value("${community.path.domain}")
    private String domain;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public User findUserById(int id) {
        return userMapper.selectById(id);
    }

    @Override
    public Map<String, Object> register(User user) {
        Map<String, Object> map = new HashMap<>();

        //先判断user
        if (user==null)
        {
            throw new IllegalArgumentException("注册信息不能为空");
        }
        if (StringUtils.isEmpty(user.getUsername()))
        {
            map.put("usernameMsg", "账号不能为空");
            return map;
        }
        if (StringUtils.isEmpty(user.getPassword()))
        {
            map.put("passwordMsg", "密码不能为空");
            return map;
        }
        if (StringUtils.isEmpty(user.getEmail()))
        {
            map.put("emailMsg", "注册邮箱不能为空");
            return map;
        }

        //验证账号：在数据库中根据名称查找是否存在传入的数据。
        User selectByName = userMapper.selectByName(user.getUsername());
        if (selectByName!=null)
        {
            //数据库中存在当前的值。则不能注册
            map.put("usernameMsg","账号已存在，不能重复注册");
            return map;
        }

        //验证账号：在数据库中根据邮箱查找是否存在传入的数据。
        User selectByEmail = userMapper.selectByEmail(user.getEmail());
        if (selectByEmail!=null)
        {
            map.put("emailMsg", "邮箱已经被注册");
            return map;
        }

        ////所有信息都检测完成，现在可以注册用户，将信息写入到数据库中了。
        //在user中的数据salt进行补充，赋值一个随机的字符串
        user.setSalt(CommunityUtil.generaterUUID().substring(0,5));//附加字符串只要5位长。
        user.setPassword(CommunityUtil.MD5(user.getPassword()+user.getSalt()));
        user.setType(0);//普通用户
        user.setStatus(0);//未激活
        user.setActivationCode(CommunityUtil.generaterUUID());
        user.setHeaderUrl(String.format("http://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());

        //将user插入到数据库中
        userMapper.insertUser(user);

        //发送激活邮件
        Context context = new Context();//创建一个context对象，通过这个对象携带变量
        context.setVariable("email",user.getEmail());//将邮件发送给用户user的邮箱
        //设置url，希望服务器用什么路径处理这个请求。
        //路径为：
        // http://localhost:8088/community/activation激活的功能/101user的id/code激活码
        String url=domain+contextPath+"/activation/"+user.getId()+"/"+user.getActivationCode();
        context.setVariable("url",url);

        //有了context后就可以用模板引擎生成邮件内容
        String email = templateEngine.process("/mail/activation", context);
        //调用mailClient的send函数发送邮件
        mailClient.sendMail(user.getEmail(),"激活邮件",email);
        //注册成功则map为空

        return map;
    }

    //激活账号
    public int activation(int userId,String code)//参数是用户id和激活码
    {
        //通过userid查询到用户
        User user = userMapper.selectById(userId);

        //判断当前用户是否已经激活
        if (user.getStatus()==1)//重复激活
            return ACTIVATION_REPEAT;

        //判断user中的激活码和参数中的激活码是否相同
        else if (user.getActivationCode().equals(code))
        {
            //修改用户的激活状态
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }

        //激活码不同
        else
            return ACTIVATION_FAILURE;
    }

    //登录功能
    @Override
    public Map<String, Object> login(String username, String password, int expiredSeconds) {

        HashMap<String, Object> map = new HashMap<>();

        //空值处理
        if (StringUtils.isBlank(username))
        {
            map.put("usernameMsg","账号不能为空");
            return map;
        }
        if (StringUtils.isBlank(password))
        {
            map.put("passwordMsg","密码不能为空");
            return map;
        }

        //检验是否合法
        //根据传入的数据从数据库查询，看是否有响应的用户，如果有并且传入的值和数据库的值相等则是合法的
        User user = userMapper.selectByName(username);
        if (user==null)
        {
            map.put("usernameMsg","账号错误❌");
            return map;
        }
        //如果user不为空则判断当查询到的用户user是否已经激活了
        if (user.getStatus()==0)
        {
            map.put("usernameMsg","账号没有激活，快去邮箱激活吧😊");
            return map;
        }
        //如果用户存在，并且激活了，则此时判断密码是否正确
        //将传入的明文密码进行加密
        password  = CommunityUtil.MD5(password+user.getSalt());
        if (!user.getPassword().equals(password))
        {
            map.put("passwordMsg","密码错误");
            return map;
        }

        //到这里动没有出错，则表明可以成功登录
        //生成登录凭证
        LoginTicket ticket = new LoginTicket();
        ticket.setTicket(CommunityUtil.generaterUUID());
        ticket.setUserId(user.getId());
        ticket.setStatus(0);//0表示登录有效，1表示登录无效
        ticket.setExpired(new Date(System.currentTimeMillis()+expiredSeconds*1000));

        //写入数据库
        loginTicketMapper.insertTicket(ticket);

        map.put("ticket",ticket.getTicket());

        return map;
    }

    @Override
    public void logOut(String ticket) {
        int status = loginTicketMapper.updateStatus(ticket, 1);
    }

    @Override
    public LoginTicket findLoginTicket(String ticket) {
        return loginTicketMapper.selectByTicket(ticket);
    }

    @Override
    public int updateHeader(int userId, String headerUrl) {
        int i = userMapper.updateHeader(userId, headerUrl);
        return i;
    }

    @Override
    public int updatePwd(int userId, String newPwd) {
        return userMapper.updatePassword(userId,newPwd);
    }

    @Override
    public User selectByemail(String email) {
        User user = userMapper.selectByEmail(email);
        return user;
    }

    @Override
    public User findUserByName(String username) {
        return userMapper.selectByName(username);
    }
}
