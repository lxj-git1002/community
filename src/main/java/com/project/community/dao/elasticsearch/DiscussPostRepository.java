package com.project.community.dao.elasticsearch;

import com.project.community.entity.DiscussPost;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
//repository是针对数据访问层的注解。mapper是mysql的专门的注解
public interface DiscussPostRepository extends ElasticsearchRepository <DiscussPost,Integer> {
    //继承ElasticsearchRepository，需要声明这个接口处理的实体类是什么DiscussPost，主键的类型是什么Integer
}
