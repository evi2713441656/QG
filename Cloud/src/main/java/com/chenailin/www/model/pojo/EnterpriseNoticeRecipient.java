package com.chenailin.www.model.pojo;

import java.util.Date;

/**
 * Enterprise notice recipient entity
 * @author evi
 */
public class EnterpriseNoticeRecipient {
    private Long id;
    private Long noticeId;
    private Long userId;
    private boolean isRead;
    private Date readTime;

    public EnterpriseNoticeRecipient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNoticeId() {
        return noticeId;
    }

    public void setNoticeId(Long noticeId) {
        this.noticeId = noticeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
}