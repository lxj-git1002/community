package com.project.community.controller.interceptor;

import com.project.community.annotation.LoginCheck;
import com.project.community.entity.User;
import com.project.community.util.HostHolder;
import org.apache.catalina.Host;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.invoke.MethodHandle;
import java.lang.reflect.Method;

//这个拦截器用来处理logincheck注解的逻辑
@Component
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Autowired
    private HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //如果通过hostholder能获得当前的用户，则表明已经登陆了，不进行拦截，否则进行拦截

        //判断当前拦截的是不是方法
        if (handler instanceof HandlerMethod)
        {
            //如果当前拦截的handler是HandlerMethod,则将当前的handler转型
            HandlerMethod handlerMethod = (HandlerMethod) handler;

            //得到了当前的handlermethod就可以得到当前的函数method
            Method method = handlerMethod.getMethod();

            //通过method就可以获得到当前函数的注解
            LoginCheck check = method.getAnnotation(LoginCheck.class);

            //如果没有logincheck则check不为空，则需要对当前的方法进行拦截
            if (check!=null)
            {
                //判断当前的用户是否为空
                if (hostHolder.getUser()==null)
                {
                    //当前没有登陆则进行拦截
                    //重定向到登录页面(通过response进行重定向)
                    //通过当前的request得到域名
                    response.sendRedirect(request.getContextPath()+"/login");
                    return false;
                }
            }
        }
        //只有当前函数有logincheck注解，并且没有登陆才进行拦截，其余情况都不进行拦截。
        return true;
        //对拦截器进行配置。在config文件夹中
    }
}
