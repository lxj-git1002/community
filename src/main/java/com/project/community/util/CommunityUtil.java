package com.project.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {

    /*
     * @Description //生成随机字符串，用于验证码，随机的名字等
     * @Param []
     * @return java.lang.String
     * @Author lxj
     * @Date 2020.10.22 23.59
     */
    public static String generaterUUID()
    {
        return UUID.randomUUID().toString().replaceAll("-","");//生成的字符串可能有-，全部替换为空”“
    }

    //md5 加密（只能加密，不能解密，每次加密都是一样的）
    //对密码加一个随机的字符串，然后进行md5加密。
    /*
     * @Description md5加密
     * @Param [key]
     * @return java.lang.String
     * @Author lxj
     * @Date 2020.10.22 23.59
     */
    public static String MD5(String key)
    {
        if (StringUtils.isEmpty(key))
            return null;
        //加密
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    /*
     * @Description 处理json数据
     * @Param int code, String msg, Map<String,Object> map
     * @return String
     * @Author lxj
     * @Date 2020.10.22 23.58
     */
    public static String getJsonString(int code, String msg, Map<String,Object> map)
    {
        //将参数封装到json中
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);

        //对map进行判断
        if (map!=null)
        {
            //遍历map
            for (String s : map.keySet()) {
                json.put(s,map.get(s));
            }
        }

        //将数据放入到json中，将json转string
        return json.toJSONString();
    }

    //overload
    public static String getJsonString(int code, String msg)
    {
        return getJsonString(code,msg,null);
    }

    public static String getJsonString(int code)
    {
        return getJsonString(code,null,null);
    }

}
