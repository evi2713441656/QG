package com.chenailin.www.model.vo;

import java.util.Date;
import java.util.List;

/**
 * @author evi
 */
public class CommentVO {
    private Long id;
    private String content;
    private Long articleId;
    private Long userId;
    private String userName;
    private String userAvatar;
    private Long parentId;
    private Date createTime;
    private List<CommentVO> replies;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public List<CommentVO> getReplies() {
        return replies;
    }

    public void setReplies(List<CommentVO> replies) {
        this.replies = replies;
    }
}