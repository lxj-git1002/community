package com.project.community.config;

import com.project.community.controller.interceptor.LoginCheckInterceptor;
import com.project.community.controller.interceptor.LoginTicketInterceptor;
import com.project.community.controller.interceptor.TestInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMVCConfig implements WebMvcConfigurer {

    //拦截器的配置

    //先注入拦截器然后进行配置
    @Autowired
    private TestInterceptor testInterceptor;

    @Autowired
    private LoginTicketInterceptor loginTicketInterceptor;

    @Autowired
    private LoginCheckInterceptor loginCheckInterceptor;

    //实现这个方法对拦截器方法进行配置
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //registry.addInterceptor(testInterceptor);//拦截一切请求
        //registry.addInterceptor(testInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");
        //排除的路径:排除css js 图片等静态文件，其余路径都拦截
        registry.addInterceptor(testInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg")
                .addPathPatterns("/register","/login");//只拦截register 和 login 两个路径


        registry.addInterceptor(loginTicketInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");//拦截所有路径

        registry.addInterceptor(loginCheckInterceptor)
                .excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg","/**/*.jpeg");

    }
}
