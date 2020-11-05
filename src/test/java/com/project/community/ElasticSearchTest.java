package com.project.community;

import com.project.community.dao.DiscussPostMapper;
import com.project.community.dao.elasticsearch.DiscussPostRepository;
import com.project.community.entity.DiscussPost;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticSearchTest {

    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussRepository;

    @Autowired
    private ElasticsearchTemplate template;

    @Test
    public void test()
    {
        //将mysql中的discusspost表的数据添加到es服务器中
        DiscussPost post = discussPostMapper.selectDiscussPostById(1);
        discussRepository.save(post);

    }
}
