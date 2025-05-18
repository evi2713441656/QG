package com.chenailin.www.model.pojo;

import java.util.Date;

/**
 * Notification entity
 * @author evi
 */
public class Notification {
    private Long id;
    private Long userId;
    private String type;
    private String content;
    private Long relatedId;
    private boolean isRead;
    private Date readTime;
    private Date createTime;

    // Notification types constants
    public static final String TYPE_ARTICLE_LIKE = "article_like";
    public static final String TYPE_ARTICLE_COMMENT = "article_comment";
    public static final String TYPE_COMMENT_REPLY = "comment_reply";
    public static final String TYPE_PRIVATE_MESSAGE = "private_message";
    public static final String TYPE_ENTERPRISE_NOTICE = "enterprise_notice";
    public static final String TYPE_SYSTEM_MESSAGE = "system_message";

    public Notification() {
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(Long relatedId) {
        this.relatedId = relatedId;
    }

    public boolean isIsRead() {
        return isRead;
    }

    public void setIsRead(boolean isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}