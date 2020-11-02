package com.project.community;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class KafkaTest {

    @Autowired
    private KafkaProducer producer;

    @Test
    public void test()
    {
        //发送消息
        producer.send("test","ojbk");
        producer.send("test","haha");
        producer.send("test","干嘛");

        try
        {
            Thread.sleep(1000*10);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}

//生产者是主动调用方法来发送消息的。想什么时候调用就什么时候调用
@Component
class KafkaProducer
{
    //注入工具
    @Autowired
    private KafkaTemplate template;

    public void send(String topic,String content)
    {
        template.send(topic,content);
    }
}

//消费者是被动处理消息的，一旦队列中有消息就会自动处理
@Component
class KafkaConsumer{

    @KafkaListener(topics = {"test"})
    public void handleMessage(ConsumerRecord record)
    {
        String s = (String) record.value();
        System.out.println(s);
    }
}