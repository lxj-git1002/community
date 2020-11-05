package com.project.community;

import org.junit.Test;
import org.springframework.beans.BeansException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
//测试环境也引用正式环境的配置类CommunityApplication
@ContextConfiguration(classes = CommunityApplication.class)

//如果哪个类想得到spring容器就实现ApplicationContextAware接口
class CommunityApplicationTests implements ApplicationContextAware {

	private ApplicationContext applicationContext;//这就是一个spring 容器

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;//spring 容器
	}

	@Test
	public void testApplicationContext()
	{
		System.out.println(applicationContext);
	}

}
