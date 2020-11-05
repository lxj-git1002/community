package com.project.community;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication//这个注解表明这个类CommunityApplication是一个配置文件
@MapperScan(value = "com.project.community.dao")
public class CommunityApplication {

	@PostConstruct
	//这个注解修饰的方法会在构造器执行完执行。主要用来管理bean的初始化的方法
	public void init()
	{
		//在初始化方法中设置，用来解决netty启动冲突的问题。
		//因为redis和elasticsearch两个服务器启动底层都是调用netty服务器的启动。netty如果有一个启动后，然后在启动一个会报错。
		//所以在这里设置，让redis和es启动时，两次调用netty服务器启动不会报错。

		//查看netty4Utils.setAvailableProcessors()源码，就可以找到这里的这个解决方案
		System.setProperty("es.set.netty.runtime.available.processors","false");
	}

	public static void main(String[] args) {
		SpringApplication.run(CommunityApplication.class, args);
	}

}