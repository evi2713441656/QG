package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.UserRelation;

import java.util.List;

/**
 * @author evi
 */
public interface UserRelationDao {
    List<UserRelation> findFollowing(Long followerId, int limit, int offset);
    List<UserRelation> findFollowers(Long followingId, int limit, int offset);
    boolean isFollowing(Long followerId, Long followingId);
    void save(UserRelation relation);
    void delete(Long followerId, Long followingId);
    int countFollowing(Long followerId);
    int countFollowers(Long followingId);
    List<Long> findCommonFollowing(Long userId1, Long userId2, int limit);
}