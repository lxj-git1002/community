package com.project.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER = "like:user";
    private static final String PREFIX_FOLLOWEE = "followee";//A 关注了 B，则followee就是在A中记录B
    private static final String PREFIX_FOLLOWER = "follower";//A 关注了 B，则follower就是在B中记录我被关注的数量，即就是用于统计B的粉丝数量。用于在主页显示
    private static final String PREFIX_KAPTCHA = "kaptcha"; //用redis存储验证码
    private static final String PREFIX_TICKET = "ticket"; //用redis存储登录凭证
    private static final String PREFIX_USERCATCH = "usercatch";

    //生成某个实体对象的👍
    //所以：
    //存入redis中的数据为
    //key：like:entity:entityType:entityId
    //value: set(userId)
    //将为这个评论或者为这个帖子点赞的用户的id存入到 set 集合中。 可以很容易得到一共有多少用户点赞，并且可以通过userId知道谁点了赞
    public static String getLikeEntityKey(int entityType,int entityId)//存入实体的类型和实体的id。类型包括是评论或者帖子
    {
        return PREFIX_ENTITY_LIKE+SPLIT+entityType+SPLIT+entityId;
    }
    
    /*
     * @Description 某一个用户收到的赞
     * @Param
     * @return 
     * @Author lxj
     * @Date 2020.10.28 21.26
     */
    //key: like:user:userId
    //value: int
     public static String getLikeUserKey(int userId)
     {
         return PREFIX_USER+SPLIT+userId;
     }

     // A关注了B（B可以是一个帖子也可以是一个用户）
    // key：
    // followee：userid（A）：entityType（关注的物体的类型）
    // value：
    // zset（entityId（B），关注的时间作为分数）
    public static String getFolloweeKey(int userId,int entityType)
    {
        return PREFIX_FOLLOWEE+SPLIT+userId+SPLIT+entityType;
    }

    //B被关注，则B的粉丝数量
    //key：
    //follower：entityType：entityId
    //value：
    //zset（userid，分数为被关注的时间）
    public static String getFollowerKey(int entityType,int entityId)
    {
        return PREFIX_FOLLOWER+SPLIT+entityType+SPLIT+entityId;
    }

    //生成登录验证码的key
    public static String getKaptchaKey(String owner)
    {
        return PREFIX_KAPTCHA+SPLIT+owner;
    }

    //生成登录凭证的key
    public static String getTicketKey(String ticket)
    {
        return PREFIX_TICKET+SPLIT+ticket;
    }

    //生成缓存 key
    public static String getUserCatchKey(int userId)
    {
        return PREFIX_USERCATCH+SPLIT+userId;
    }
}
