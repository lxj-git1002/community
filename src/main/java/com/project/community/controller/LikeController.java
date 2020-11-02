package com.project.community.controller;

import com.project.community.annotation.LoginCheck;
import com.project.community.entity.Event;
import com.project.community.entity.User;
import com.project.community.event.EventProducer;
import com.project.community.service.LikeService;
import com.project.community.util.CommunityConstant;
import com.project.community.util.CommunityUtil;
import com.project.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

@Controller
public class LikeController implements CommunityConstant {

    @Autowired
    private LikeService likeService;

    @Autowired
    private HostHolder hostHolder;

    //用于产生事件
    @Autowired
    private EventProducer producer;

    //没有登录不许点赞，用拦截器进行拦截
    @LoginCheck
    @RequestMapping(path = "/like",method = RequestMethod.POST)
    //点赞是一个异步请求，不用刷新整个页面，所以用异步
    @ResponseBody
    public String like(int entityType,int entityId,int entityUserId,int postId)
    {
        //当前用户点赞，则先获得当前用户
        User user = hostHolder.getUser();
        //点赞
        likeService.like(user.getId(),entityType,entityId,entityUserId);
        //统计数量
        long num = likeService.findEntityLikeNum(entityType, entityId);
        //状态
        int status = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);

        //将点赞的数量和状态传到前端
        //将返回的结果存入到map中
        HashMap<String, Object> map = new HashMap<>();
        map.put("likeCount",num);
        map.put("likeStatus",status);

        //点赞完成系统发送消息
        //如果是取消赞则不发送消息
        if (status==1)
        {
            //设置事件
            Event event = new Event()
                    .setTopic(TOPIC_LIKE)
                    .setUserId(hostHolder.getUser().getId())
                    .setEntityType(entityType)
                    .setEntityId(entityId)
                    .setEntityUserId(entityUserId)
                    .setData("postId",postId);
            producer.fireEvent(event);
        }

        //给前端返回一个json的数据
        return CommunityUtil.getJsonString(0,null,map);
    }
}
