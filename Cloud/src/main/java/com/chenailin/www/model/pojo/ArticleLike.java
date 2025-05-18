package com.chenailin.www.model.pojo;

import java.util.Date;

/**
 * 文章点赞实体类
 * @author evi
 */
public class ArticleLike {
    private Long id;
    private Long articleId;
    private Long userId;
    private Date createTime;

    public ArticleLike() {
    }

    public ArticleLike(Long id, Long articleId, Long userId, Date createTime) {
        this.id = id;
        this.articleId = articleId;
        this.userId = userId;
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ArticleLike{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", createTime=" + createTime +
                '}';
    }
}