package com.project.community;

import io.netty.util.concurrent.NonStickyEventExecutorGroup;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {

    @Autowired
    private RedisTemplate<String,Object> template;

    @Test
    public void testString()
    {
        String key="test:count";

        //给数据库中存入数据
        template.opsForValue().set(key,2);

        //从数据库中取出数据
        System.out.println(template.opsForValue().get(key));

        //将test:count 增加
        System.out.println(template.opsForValue().increment(key));

        //将test:count 减少
        System.out.println(template.opsForValue().decrement(key));
    }

    @Test
    public void testHashs()
    {
        String key = "test:user";

        template.opsForHash().put(key,"id",1);
        template.opsForHash().put(key,"name","lxj");

        System.out.println(template.opsForHash().get(key,"id"));
        System.out.println(template.opsForHash().get(key,"name"));

    }

    @Test
    public void testLists()
    {

        String key = "test:ids";

        template.opsForList().leftPush(key,101);
        template.opsForList().leftPush(key,102);
        template.opsForList().leftPush(key,103);

        System.out.println(template.opsForList().size(key));
        System.out.println(template.opsForList().index(key,2));
        System.out.println(template.opsForList().range(key,1,2));

        System.out.println(template.opsForList().rightPop(key));
        System.out.println(template.opsForList().leftPop(key));
    }

    @Test
    public void testSets()
    {
        String key = "test:teachers";

        template.opsForSet().add(key,"aaaa","bbbb","cccc","dddd");

        System.out.println(template.opsForSet().size(key));
        System.out.println(template.opsForSet().members(key));
        System.out.println(template.opsForSet().pop(key));
    }

    @Test
    public void testZsets()
    {
        String key = "test:student";

        template.opsForZSet().add(key,"aaa",98);
        template.opsForZSet().add(key,"bbb",100);
        template.opsForZSet().add(key,"ccc",95);
        template.opsForZSet().add(key,"ddd",94);

        System.out.println(template.opsForZSet().size(key));
        System.out.println(template.opsForZSet().zCard(key));
        System.out.println(template.opsForZSet().score(key,"bbb"));
        System.out.println(template.opsForZSet().rank(key,"ccc"));
        System.out.println(template.opsForZSet().reverseRank(key,"ccc"));
        System.out.println(template.opsForZSet().range(key,1,3));
        System.out.println(template.opsForZSet().rangeByScore(key,1,300));
        System.out.println(template.opsForZSet().range(key,0,1));
    }

    @Test
    public  void  test()
    {
        //为常用的key绑定数据。这样在访问操作数据的时候就不用反复传入key
        String key = "test:count";
        BoundValueOperations<String, Object> ops = template.boundValueOps(key);
        ops.increment();
        ops.increment();
        System.out.println(ops.get());
    }

    //编程式事务
    @Test
    public void testTransactional()
    {
        Object obj = template.execute(new SessionCallback<Object>() {
            @Override
            public Object execute(RedisOperations operations) throws DataAccessException {
                String key = "test:tx";

                //启动事务
                operations.multi();

                //操作
                //添加数据
                operations.opsForSet().add(key,"aaa");
                operations.opsForSet().add(key,"ccc");
                operations.opsForSet().add(key,"bbb");

                //启动和提交事务之间的命令不会马上执行，会将命令放到队列中，所以这里的查询没有效果
                //查询数据
                System.out.println(operations.opsForSet().members(key));

                //提交事务
                return operations.exec();
            }
        });
        System.out.println(obj);
    }

}
