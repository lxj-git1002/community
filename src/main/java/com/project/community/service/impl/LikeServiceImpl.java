package com.project.community.service.impl;

import com.project.community.service.LikeService;
import com.project.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class LikeServiceImpl implements LikeService {

    @Autowired
    private RedisTemplate<String,Object> template;

    //点赞
    @Override
    //userid是当前用户的id，这个是点赞的人
    //entityuserid是发当前帖子的人的id。这个是被点赞帖子等的创作者的id
    public void like(int userId,int entityType,int entityId,int entityUserId) {

//        String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType,entityId);
//
//        //判断当前用户是否以及点过赞，如果已经点过赞则为取消，如果还没有点过则为点赞
//        //通过判断userid是否在redis中
//        boolean isMember = template.opsForSet().isMember(entityLikeKey, userId);
//        if (isMember)
//        {
//            //已经点过赞，则取消
//            //将userid从redis中删除
//            template.opsForSet().remove(entityLikeKey,userId);
//        }
//        else
//        {
//            //还没有点赞，则点赞
//            template.opsForSet().add(entityLikeKey,userId);
//        }

        template.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {

                //创建key
                String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType,entityId);
                String userLikeKey = RedisKeyUtil.getLikeUserKey(entityUserId);

                //判断当前用户是否已经点赞
                boolean isMember = operations.opsForSet().isMember(entityLikeKey, userId);
                //这里的查询要提前查询，因为redis如果开启事务到事务提交不会立即执行查询操作，而是将查询命令放在队列中，等事务提交后在查询

                //开启事务
                operations.multi();

                //判断是否已经点过赞
                if (isMember)
                {
                    //取消赞
                    operations.opsForSet().remove(entityLikeKey,userId);
                    //同时取消entityUser收到的点赞,即entityuser收到的所有赞减少一个
                    operations.opsForValue().decrement(userLikeKey);
                }
                else
                {
                    //点赞
                    operations.opsForSet().add(entityLikeKey,userId);
                    //entityuser 收到点赞，则数量加1
                    operations.opsForValue().increment(userLikeKey);
                }

                //提交事务
                return operations.exec();
            }
        });
    }

    //查询某个实体被但赞的数量
    @Override
    public long findEntityLikeNum(int entityType, int entityId) {

        String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType,entityId);
        return template.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体点赞的状态
    @Override
    public int findEntityLikeStatus(int userId, int entityType, int entityId) {

        String entityLikeKey = RedisKeyUtil.getLikeEntityKey(entityType,entityId);
        //当前的userid如果已经点过赞则返回1。否则返回0
        return template.opsForSet().isMember(entityLikeKey, userId)?1:0;
    }

    // 查询某个user收到的点赞数量
    @Override
    public int findUserLikeNum(int userId) {
        String userLikeKey = RedisKeyUtil.getLikeUserKey(userId);
        Integer num = (Integer) template.opsForValue().get(userLikeKey);

        return num == null?0:num;
    }
}
