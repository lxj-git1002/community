package com.project.community.dao;

import com.project.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, int offset, int limit);//userid可以传入也可以不传入。传入时候表示某个人发布的贴子。不传入表示全部查询
    //offset是分页显示的功能中某一页的起始行，limit是每一页显示多少内容

    //分页显示需要知道一共有多少数据
    //@Param 注解是用来给参数取别名
    //当一个方法只有一个参数的时候，并且这个方法用在动态sql中（if标签中）则必须加这个注解
    int selectDiscussPostRows(@Param("userId") int userId);//这里的userid也是可以不传入的。不传入的情况下表示的是表中所有的数据，传入的时候表示某个人发的所以的帖子。

    //根据标签名搜索所有属于这个标签下数据、
    /*    *************************************    */
}
