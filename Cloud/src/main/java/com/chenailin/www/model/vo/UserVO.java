package com.chenailin.www.model.vo;

import com.chenailin.www.model.pojo.User;

import java.util.Date;

/**
 * @author evi
 */
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String avatar;
    private String password;
    private Date lastLoginTime;
    private Date createTime;
    // 添加关注和粉丝数量字段
    private Integer followingCount;
    private Integer followerCount;
    // 当前用户是否关注该用户
    private Boolean isFollowed;


    public Boolean getFollowed() {
        return isFollowed;
    }

    public void setFollowed(Boolean followed) {
        isFollowed = followed;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(Integer followingCount) {
        this.followingCount = followingCount;
    }

    public Integer getFollowerCount() {
        return followerCount;
    }

    public void setFollowerCount(Integer followerCount) {
        this.followerCount = followerCount;
    }

    public Boolean getIsFollowed() {
        return isFollowed;
    }

    public void setIsFollowed(Boolean isFollowed) {
        this.isFollowed = isFollowed;
    }

    public static UserVO fromEntity(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setEmail(user.getEmail());
        vo.setAvatar(user.getAvatar());
        vo.setLastLoginTime(user.getLastLoginTime());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    /**
     * 从实体转换为VO，并设置关注信息
     */
    public static UserVO fromEntityWithRelation(User user, Integer followingCount, Integer followerCount, Boolean isFollowed) {
        UserVO vo = fromEntity(user);
        vo.setFollowingCount(followingCount);
        vo.setFollowerCount(followerCount);
        vo.setIsFollowed(isFollowed);
        return vo;
    }
}