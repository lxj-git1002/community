package com.project.community;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.dao.LoginTicketMapper;
import com.project.community.dao.UserMapper;
import com.project.community.entity.DiscussPost;
import com.project.community.entity.LoginTicket;
import com.project.community.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    /*
     * 测试usermapper.xml文件是否正确
     * */
    @Autowired
    private UserMapper userMapper;

    @Test
    public void test1()
    {
        User user = new User();
        user.setUsername("lxj");
        user.setEmail("lxj@outlook.com");
        user.setPassword("1002");
        user.setHeaderUrl("file://C:/Users/lenovo/Desktop/test.jpg");
        user.setCreateTime(new Date());
        user.setSalt("abc");
        user.setActivationCode("2222");
//        user.setStatus(1);
//        user.setType(1);
//        user.setId(1);

        int isSuccess = userMapper.insertUser(user);
        System.out.println(isSuccess);
        System.out.println(user.getId());
    }

    @Test
    public void test2()
    {
        User user = userMapper.selectById(1);
        System.out.println(user);
    }

    @Test
    public void test3()
    {
        int i = userMapper.updatePassword(101,"111");
        User user = userMapper.selectById(i);
        System.out.println(user);
    }

    /*
     * 测试discusspostmapper.xml是否正确
     * */
    @Autowired
    private DiscussPostMapper discussPostMapper;
    @Test
    public void test4()
    {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0, 0, 3);
        for (DiscussPost discussPost : list) {
            System.out.println(discussPost);
        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }

    @Autowired
    private LoginTicketMapper loginTicketMapper;

    @Test
    public void test5()
    {
        LoginTicket ticket = new LoginTicket();
        ticket.setUserId(101);
        ticket.setExpired(new Date(System.currentTimeMillis()+1000));
        ticket.setStatus(0);
        ticket.setTicket("111");
        loginTicketMapper.insertTicket(ticket);
    }

    @Test
    public void test6()
    {
        LoginTicket loginTicket = loginTicketMapper.selectByTicket("111");
        System.out.println(loginTicket);
        int i = loginTicketMapper.updateStatus("111", 1);
    }
}