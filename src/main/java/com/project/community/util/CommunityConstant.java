package com.project.community.util;

public interface CommunityConstant {

    /*激活的状态
    * 1 激活成功
    * 2 重复激活
    * 3 激活失败
    * 4 默认状态登录超时时间
    * 5 记住状态登录超时时间
    * 6 实体类型 帖子1
    * 7 实体类型 评论2
    * */

    int ACTIVATION_SUCCESS = 0;
    int ACTIVATION_REPEAT = 1;
    int ACTIVATION_FAILURE = 2;
    int DEFAULT_EXPIRED_SECONDS = 3600*5;//5 hours
    int REMEMBER_EXPIRED_SECONDS = 3600*24*15; //1 month
    int ENTITY_TYPE_POST = 1;
    int ENTITY_TYPE_COMMENT = 2;
}
