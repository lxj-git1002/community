package com.project.community.util;

//这个类提供一个发邮件的功能，不过不是真正的发邮件而是将发邮件这个具体的事情委托给 网易邮箱来完成

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.internet.MimeMessage;
import javax.swing.text.html.HTML;

@Component//表示这个类需要由spring容器管理,将这个类创建一个公用的bean，不管在那个层次都可以使用
public class MailClient {

    //记录日志
    private static final Logger LOGGER = LoggerFactory.getLogger(MailClient.class);

    //发送邮件需要用到的核心组件javamailsender，直接通过spring注入到当前的bean中就可以直接使用
    @Autowired
    private JavaMailSender mailSender;

    //通过服务器发送邮件，发送人是固定的，直接注入到当前的bean中
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to,String subject,String content)//发送方法，参数为接受者，邮件主题，邮件内容
    {
        try {
            //构建MimeMessage
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);

            //设置发件人 收件人 主题 内容
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);//发送html类型的邮件

            mailSender.send(helper.getMimeMessage());
        }
        catch (Exception e)
        {
            LOGGER.error("发送邮件失败！！！"+e.getMessage());
        }
    }
}
