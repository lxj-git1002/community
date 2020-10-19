package com.project.community.controller.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
//将这个类交给Spring容器管理
public class TestInterceptor implements HandlerInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //这个方法在controller处理请求之前进行拦截。
        //return false表示取消controller请求，请求被拦截不会继续执行
        //return true 表示执行controller请求，请求没有被拦截

        LOGGER.debug("preHandle"+handler.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //在controller之后执行，模版引擎执行之前执行
        LOGGER.debug("postHandle"+handler.toString());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        //在程序最后，模版引擎执行之后执行
        LOGGER.debug("afterCompletion"+handler.toString());
    }
}
