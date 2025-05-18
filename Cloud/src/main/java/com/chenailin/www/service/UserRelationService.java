package com.chenailin.www.service;

import com.chenailin.www.model.vo.UserVO;

import java.util.List;

public interface UserRelationService {
    void follow(Long followerId, Long followingId);

    void unfollow(Long followerId, Long followingId);

    List<UserVO> getFollowing(Long userId, int page, int size);

    List<UserVO> getFollowers(Long userId, int page, int size);

    boolean isFollowing(Long followerId, Long followingId);

    int countFollowing(Long userId);

    int countFollowers(Long userId);

    List<UserVO> getCommonFollowing(Long userId1, Long userId2, int limit);
}
