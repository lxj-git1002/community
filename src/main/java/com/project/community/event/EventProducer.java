package com.project.community.event;

import com.alibaba.fastjson.JSONObject;
import com.project.community.entity.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EventProducer {

    @Autowired
    private KafkaTemplate template;

    //处理事件
    public void fireEvent(Event event)
    {
        //将event包含的信息发送到指定的主题
        template.send(event.getTopic(), JSONObject.toJSONString(event));
    }
}
