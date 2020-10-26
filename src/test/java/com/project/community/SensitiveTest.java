package com.project.community;

import com.project.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class SensitiveTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void test1()
    {
        String s="👴今天要👨杀😄人👩吃屎👨，赌博，哈哈哈😂";
        String res = sensitiveFilter.filter(s);
        System.out.println(s);
        System.out.println(res);
    }
}
