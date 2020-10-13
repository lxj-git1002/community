package com.project.community.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.UUID;

public class CommunityUtil {

    //生成随机字符串，用于验证码，随机的名字等
    public static String generaterUUID()
    {
        return UUID.randomUUID().toString().replaceAll("-","");//生成的字符串可能有-，全部替换为空”“
    }

    //md5 加密（只能加密，不能解密，每次加密都是一样的）
    //对密码加一个随机的字符串，然后进行md5加密。
    public static String MD5(String key)
    {
        if (StringUtils.isEmpty(key))
            return null;
        //加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }
}
