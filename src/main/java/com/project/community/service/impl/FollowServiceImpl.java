package com.project.community.service.impl;

import com.project.community.service.FollowService;
import com.project.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private RedisTemplate<String,Object> template;

    @Override
    public void follow(int userId, int entityType, int entityId) {

        template.execute(new SessionCallback() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {

                //key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                //启动事务
                operations.multi();

                //存入数据
                operations.opsForZSet().add(followeeKey,entityId,System.currentTimeMillis());
                operations.opsForZSet().add(followerKey,userId,System.currentTimeMillis());

                return operations.exec();
            }
        });
    }

    @Override
    public void unfollow(int userId, int entityType, int entityId) {

        template.execute(new SessionCallback() {
            @Override
            public  Object execute(RedisOperations operations) throws DataAccessException {

                //key
                String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
                String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);

                //启动事务
                operations.multi();

                //存入数据
                operations.opsForZSet().remove(followeeKey,entityId);
                operations.opsForZSet().remove(followerKey,userId);

                return operations.exec();
            }
        });
    }

    @Override
    public long findFolloweeNum(int userId, int entityType) {

        //key
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);
        return template.opsForZSet().zCard(followeeKey);
    }

    @Override
    public long findFollowerNum(int entityType, int entityId) {

        String followerKey = RedisKeyUtil.getFollowerKey(entityType, entityId);
        return template.opsForZSet().zCard(followerKey);
    }

    @Override
    public boolean hasFollow(int userId, int entityType, int entityId) {

        //创建key，通过这个key查看zset中是否有entityId这个对象
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,entityType);

        //如果查询里面如果返回为空，则表示没有关注，否则表示已经关注
        return template.opsForZSet().score(followeeKey, entityId)==null?false:true;
    }
}
