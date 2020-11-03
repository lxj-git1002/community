package com.project.community.controller.interceptor;

import com.project.community.entity.User;
import com.project.community.service.MessageService;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class MessageInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private MessageService messageService;

    //在controller之后，模版之前调用。进行拦截
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if (user!=null&&modelAndView!=null)
        {
            //未读消息数量
            int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
            int unreadNoticeCount = messageService.findUnreadNoticeCount(user.getId(), null);
            modelAndView.addObject("totalUnread",letterUnreadCount+unreadNoticeCount);
        }
    }
}
