package com.project.community;

import com.project.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSendTest {

    @Autowired
    private MailClient mailClient;

    //注入模板引擎
    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void test1()
    {
        mailClient.sendMail("lxj10021002@163.com","验证邮件","发送邮件成功！");
    }

    @Test
    public void testHtmlEmail()
    {
        Context context = new Context();
        context.setVariable("username","aaa");

        //调用模版引擎生成动态网页
        String email = templateEngine.process("/mail/demo", context);
        System.out.println(email);

        //发送邮件
        mailClient.sendMail("lxj10021002@163.com","验证邮件",email);
    }
}
