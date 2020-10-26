package com.project.community.service;

import com.project.community.entity.Message;

import java.util.List;

public interface MessageService {

    public List<Message> findConversations(int userId,int offset,int limit);

    public int findConversationCount(int userId);

    public List<Message> findLetters(String conversationId,int offset,int limit);

    public int findLetterCount(String conversationId);

    public int findLetterUnreadCount(int userId,String conversationId);

    public int addMessage(Message message);

    public int alterMessageStatus(List<Integer> ids,int status);
}
