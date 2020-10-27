package com.project.community.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory)//spring 会自动注入连接工程这个bean
    {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        //将连接工厂设置给template的连接工厂
        template.setConnectionFactory(factory);

        //设置redis的序列化方式
        //设置key的序列化方式
        template.setKeySerializer(RedisSerializer.string());

        //设置普通的value的序列化方式
        template.setValueSerializer(RedisSerializer.json());

        //设置hash类型的value的序列化方式
             //设置hash类型的key的序列化方式
        template.setHashKeySerializer(RedisSerializer.string());

             //设置hash类型的value的序列化方式
        template.setHashValueSerializer(RedisSerializer.json());

        //让设置生效
        template.afterPropertiesSet();

        return template;
    }
}
