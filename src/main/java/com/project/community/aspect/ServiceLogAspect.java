package com.project.community.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Aspect
public class ServiceLogAspect {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceLogAspect.class);

    //切入点
    @Pointcut("execution(* com.project.community.service.*.*(..))")
    public void pointcut()
    { }

    //在调用业务方法之前记录日志，
    //日志内容：某人（ip地址） 在什么时候访问了什么方法
    @Before("pointcut()")
    public void before(JoinPoint joinpoint)
    {
        //用户ip地址在时间访问了com.project.community.service.xxx方法
        //获取用户ip
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes==null)
        {
            return;
        }
        HttpServletRequest request = attributes.getRequest();
        String ip = request.getRemoteHost();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        //得到方法的名
        String methodName = joinpoint.getSignature().getDeclaringTypeName() + "." + joinpoint.getSignature().getName();

        LOGGER.info(String.format("用户[%s]，在[%s]时间，访问了[%s]",ip,time,methodName));
    }
}
