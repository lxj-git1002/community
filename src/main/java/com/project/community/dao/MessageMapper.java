package com.project.community.dao;

import com.project.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageMapper {

    //查询当前用户会话列表（当前用户和不同用户之间的消息往来），每一个会话显示最新的一个消息
    List<Message> selectConversations (int userId,int offset,int limit);

    //查询当前用户的所以会话数量，用于分页
    int selectConversationCount(int userId);

    //查询某个会话里面的所有私信列表
    List<Message> selectLetters(String conversationId,int offset,int limit);//私信太多也要支持分页

    //查询某个会话中的私信的数量，用于分页
    int selectLetterCount(String conversationId);

    //查询某一个会话的未读私信的数量
    //如果conversationId没有传入则查询当前用户的所有会话中都未读的消息的总数
    int selectLetterUnreadCount(int userId,String conversationId);

    //增加消息
    int insertMessage(Message message);

    //修改消息的状态，将未读消息，修改未已读消息，或者删除等
    int updateMessageStatus(List<Integer> ids,int status);//将很多个消息的消息状态修改未参数status
}
