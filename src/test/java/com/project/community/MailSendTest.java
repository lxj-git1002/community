package com.project.community;

import com.project.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MailSendTest {

    @Autowired
    private MailClient mailClient;

    @Test
    public void test1()
    {
        mailClient.sendMail("lxj10021002@163.com","验证邮件","发送邮件成功！");
    }
}
