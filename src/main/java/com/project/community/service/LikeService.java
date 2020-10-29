package com.project.community.service;

public interface LikeService {

    public void like(int userId,int entityType,int entityId,int entityUserId);

    public long findEntityLikeNum(int entityType,int entityId);

    public int findEntityLikeStatus(int userId,int entityType,int entityId);

    public int findUserLikeNum(int userId);

}
