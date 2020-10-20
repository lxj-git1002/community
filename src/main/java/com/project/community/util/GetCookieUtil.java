package com.project.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class GetCookieUtil {

    public static String CookieValue(HttpServletRequest request,String name)
    {
        //从request中获得名称为name 的cookie
        if (request==null||name==null)
        {
            throw new IllegalArgumentException("参数为空");
        }
        Cookie[] cookies = request.getCookies();
        for (Cookie c : cookies) {
            if (c.getName().equals(name))
                return c.getValue();
        }
        return null;
    }
}
