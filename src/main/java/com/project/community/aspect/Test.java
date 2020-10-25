package com.project.community.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

//@Component
//@Aspect
public class Test {

    //切入点
    @Pointcut("execution(* com.project.community.service.*.*(..))")
    public void pointcut()
    {

    }

    //在切点开始进行增强，例如在开始的时候就记录日志文件则用before
    @Before("pointcut()")
    public void before()
    {
        System.out.println("before");
    }

    //在之后进行增强
    @After("pointcut()")
    public void after()
    {
        System.out.println("after");
    }

    //在返回值以后进行增强
    @AfterReturning("pointcut()")
    public void afterReturning()
    {
        System.out.println("afterReturning");
    }

    //在抛出异常的时候增强
    @AfterThrowing("pointcut()")
    public void afterThrowing()
    {
        System.out.println("afterThrowing");
    }

    //即想在切点之前织入，又想在切点之后织入
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint)throws Throwable
    {
        //切点之前进行织入，然后编写程序进行增强
        System.out.println("around before");

        Object proceed = joinPoint.proceed();//分割点

        //切点之后进行织入，然后编写程序进行增强
        System.out.println("around after");
        return proceed;
    }
}
