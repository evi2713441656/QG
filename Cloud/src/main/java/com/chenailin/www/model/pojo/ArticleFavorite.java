package com.chenailin.www.model.pojo;

import java.util.Date;

/**
 * @author evi
 */
public class ArticleFavorite {
    private Long id;
    private Long articleId;
    private Long userId;
    private Date createTime;

    public ArticleFavorite() {
    }

    public ArticleFavorite(Long articleId, Date createTime, Long id, Long userId) {
        this.articleId = articleId;
        this.createTime = createTime;
        this.id = id;
        this.userId = userId;
    }

    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ArticleFavorite{" +
                "id=" + id +
                ", articleId=" + articleId +
                ", userId=" + userId +
                ", createTime=" + createTime +
                '}';
    }
}
