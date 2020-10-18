package com.project.community.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
//表明当前这个类是一个配置类
public class KaptchaConfig {

    @Bean
    //通过bean这个注解，让当前类被Spring容器管理当前bean
    public Producer KaptchaProducer()
    {
        //实例化producer这个接口的类
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        //传入参数
        Properties properties = new Properties();
        properties.setProperty("kaptcha.image.width","100");
        properties.setProperty("kaptcha.image.height","40");
        properties.setProperty("kaptcha.textproducer.font.size","32");
        properties.setProperty("kaptcha.textproducer.font.color","red");
        //字体
        properties.setProperty("kaptcha.textproducer.font.name","Courier");
        properties.setProperty("kaptcha.textproducer.char.string","ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz");
        properties.setProperty("kaptcha.textproducer.char.length","4");
        //图片样式：
        properties.setProperty("kaptcha.obscurificator.impl","com.google.code.kaptcha.impl.ShadowGimpy");

        //验证码图片的噪声
        properties.setProperty("kaptcha.noise.impl","com.google.code.kaptcha.impl.NoNoise");


        Config config = new Config(properties);
        kaptcha.setConfig(config);
        return kaptcha;
    }
}
