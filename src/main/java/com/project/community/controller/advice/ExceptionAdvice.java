package com.project.community.controller.advice;

import com.project.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@ControllerAdvice(annotations = Controller.class)
//限制ControllerAdvice扫描的范围为带有controller注解的类。其他的例如dao，service等的异常只需要抛出即可。抛出后会一直到controller层，然后处理。
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    //处理异常
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        LOGGER.error("服务器异常:"+e.getMessage());
        for (StackTraceElement element : e.getStackTrace())
        {
            LOGGER.error(element.toString());
        }

        //判断发生异常的请求是同步还是异步请求
        String xRequestedWith = request.getHeader("x-requested-with");
        if ("XMLHttpRequest".equals(xRequestedWith))
        {
            //异步请求,异步请求则响应一个字符串。同步请求响应一个页面
            response.setContentType("application/plain;charset=utf-8");//返回一个普通的字符串
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJsonString(1, "服务器异常!"));
        }
        else
        {
            //不是异步请求就重定向到页面
            response.sendRedirect(request.getContextPath()+"/error");
        }
    }
}
