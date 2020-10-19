package com.project.community.controller.interceptor;

import com.project.community.entity.LoginTicket;
import com.project.community.entity.User;
import com.project.community.service.UserService;
import com.project.community.util.GetCookieUtil;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginTicketInterceptor  implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Autowired
    private HostHolder hostHolder;

    //在请求一开是就获取浏览器cookie中保存的ticket，通过ticket查找对应的user，然后将user响应到模版中

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //通过cookie得到ticket
        String ticket = GetCookieUtil.CookieValue(request, "ticket");
        if (ticket!=null)
        {
            //获得了ticket，表示已经登录过了
            //通过这个ticket查询整个loginticket对象
            LoginTicket loginTicket = userService.findLoginTicket(ticket);

            //检查凭证是否有效
            if (loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date()))
            {
                //当凭证不为空，凭证的状态为登录状态，凭证没有过期
                //此时表示凭证loginticket有效
                //通过ticket查询user
                User user = userService.findUserById(loginTicket.getUserId());

                //后续的操作或使用到user，所以让本次操作持有用户。对user进行暂存
                //为了保证多线程隔离，多线程不能公用user，所以进行线程隔离存储user
                hostHolder.setUser(user);
            }
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //在调用模版引擎之前将user存入到model中
        User user = hostHolder.getUser();
        if (user!=null&&modelAndView!=null)
        {
            modelAndView.addObject("loginUser",user);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在所有请求结束之后清楚user
        hostHolder.clear();
    }
}
