package com.chenailin.www.model.vo;

import java.util.Date;
import java.util.List;

/**
 * View object for enterprise notice
 * @author evi
 */
public class EnterpriseNoticeVO {
    private Long id;
    private Long enterpriseId;
    private String enterpriseName;
    private String title;
    private String content;
    private Long publisherId;
    private String publisherName;
    private String publisherAvatar;
    private boolean isPrivate;
    // Whether the current user has read this notice
    private boolean isRead;
    // When the current user read this notice
    private Date readTime;
    private Date createTime;
    private Date updateTime;
    // Number of users who have read this notice
    private int readCount;
    // Total number of recipients for this notice
    private int totalRecipients;
    // Recipients if this is a private notice (for admin view)
    private List<EnterpriseMemberVO> recipients;

    public EnterpriseNoticeVO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEnterpriseId() {
        return enterpriseId;
    }

    public void setEnterpriseId(Long enterpriseId) {
        this.enterpriseId = enterpriseId;
    }

    public String getEnterpriseName() {
        return enterpriseName;
    }

    public void setEnterpriseName(String enterpriseName) {
        this.enterpriseName = enterpriseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Long getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Long publisherId) {
        this.publisherId = publisherId;
    }

    public String getPublisherName() {
        return publisherName;
    }

    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    public String getPublisherAvatar() {
        return publisherAvatar;
    }

    public void setPublisherAvatar(String publisherAvatar) {
        this.publisherAvatar = publisherAvatar;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getTotalRecipients() {
        return totalRecipients;
    }

    public void setTotalRecipients(int totalRecipients) {
        this.totalRecipients = totalRecipients;
    }

    public List<EnterpriseMemberVO> getRecipients() {
        return recipients;
    }

    public void setRecipients(List<EnterpriseMemberVO> recipients) {
        this.recipients = recipients;
    }
}