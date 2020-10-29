package com.project.community.util;

public class RedisKeyUtil {

    private static final String SPLIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity";
    private static final String PREFIX_USER = "like:user";

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

}
