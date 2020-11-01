package com.project.community.service.impl;

import com.project.community.entity.User;
import com.project.community.service.FollowService;
import com.project.community.service.UserService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FollowServiceImpl implements FollowService, CommunityConstant {

    @Autowired
    private RedisTemplate<String,Object> template;

    @Autowired
    private UserService userService;

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

    @Override
    public List<Map<String, Object>> findFollowees(int userId, int offset, int limit) {

        String followeeKey = RedisKeyUtil.getFolloweeKey(userId,ENTITY_TYPE_USER);

        //分页显示，则查出来的数据是从offset开始，一页显示limit个数据
        Set<Object> ranges = template.opsForZSet().reverseRange(followeeKey, offset, offset + limit - 1);

        if (ranges==null)
        {
            return null;
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Object range : ranges) {
            // 获取关注了的人的id
            Integer targetId = (Integer) range;
            Map<String, Object> map = new HashMap<>();
            //根据用户的id得到用户的消息
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = template.opsForZSet().score(followeeKey, targetId);
            //将关注的时间加入到map中
            map.put("followTime",new Date(score.longValue()));
            res.add(map);
        }
        return res;
    }

    //查询userid的粉丝数
    @Override
    public List<Map<String, Object>> findFollowers(int userId, int offset, int limit) {

        String followerKey = RedisKeyUtil.getFollowerKey(ENTITY_TYPE_USER, userId);

        //分页显示，则查出来的数据是从offset开始，一页显示limit个数据
        Set<Object> ranges = template.opsForZSet().reverseRange(followerKey, offset, offset + limit - 1);

        if (ranges==null)
        {
            return null;
        }

        List<Map<String, Object>> res = new ArrayList<>();
        for (Object range : ranges) {
            // 获取关注了的人的id
            Integer targetId = (Integer) range;
            Map<String, Object> map = new HashMap<>();
            //根据用户的id得到用户的消息
            User user = userService.findUserById(targetId);
            map.put("user",user);
            Double score = template.opsForZSet().score(followerKey, targetId);
            //将关注的时间加入到map中
            map.put("followTime",new Date(score.longValue()));
            res.add(map);
        }
        return res;

    }
}
