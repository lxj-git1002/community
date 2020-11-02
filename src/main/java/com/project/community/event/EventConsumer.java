package com.project.community.event;

import com.alibaba.fastjson.JSONObject;
import com.project.community.entity.Event;
import com.project.community.entity.Message;
import com.project.community.service.MessageService;
import com.project.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(EventConsumer.class);

    //向message表中插入数据
    @Autowired
    private MessageService messageService;

    //消费者处理消息
    @KafkaListener(topics = {TOPIC_COMMENT,TOPIC_FOLLOW,TOPIC_LIKE})
    public void handleMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            LOGGER.error("消息内容为空");
            return;
        }

        //将json字符串恢复成相关的对象
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null)
        {
            //record不为空但是翻译的内容错误，则返回并打印日志
            LOGGER.error("消息格式错误");
            return;
        }

        //构造message对象，发送站内通知
        Message message = new Message();
        //消息的发送者
        message.setFromId(SYSTEM_USER_ID);
        //消息的接受者
        message.setToId(event.getEntityUserId());
        //conversationid设置为topic来表明是评论，点赞还是关注
        message.setConversationId(event.getTopic());
        //时间
        message.setCreateTime(new Date());
        //内容
        HashMap<String, Object> content = new HashMap<>();
        //A点赞了B的帖子，A触发了点赞这个事，触发的事件可能是点赞，也可能是评论等。点赞的具体是哪个帖子。
        content.put("userId",event.getUserId());//这个事件是谁触发的。
        content.put("entityType",event.getEntityType());//触发的事件是什么类型。
        content.put("entityId",event.getEntityId());//点赞评论的具体是哪个帖子

        //其他的消息也放在content中
        if (!event.getData().isEmpty())
        {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));

        //数据构造完成将数据存入到数据表中
        messageService.addMessage(message);
    }
}
