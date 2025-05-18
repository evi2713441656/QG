package com.chenailin.www.model.dto;

/**
 * @author evi
 */
public class UserRelationDTO {
    private Long followerId;
    private Long followingId;

    public Long getFollowerId() {
        return followerId;
    }

    public void setFollowerId(Long followerId) {
        this.followerId = followerId;
    }

    public Long getFollowingId() {
        return followingId;
    }

    public void setFollowingId(Long followingId) {
        this.followingId = followingId;
    }
}