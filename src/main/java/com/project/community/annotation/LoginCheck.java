package com.project.community.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

//用来检查当前是否已经登录

@Target(ElementType.METHOD)
//表示自定义的注解可以作用在方法上

@Retention(RetentionPolicy.RUNTIME)
//表示自定义的注解只有在程序运行的时候有效

public @interface LoginCheck {
}
