package com.chenailin.www.model.dto;

import java.util.List;

/**
 * Data transfer object for enterprise notice
 * @author evi
 */
public class EnterpriseNoticeDTO {
    private Long id;
    private Long enterpriseId;
    private String title;
    private String content;
    private boolean isPrivate;
    // IDs of recipients if this is a private notice
    private List<Long> recipientIds;

    public EnterpriseNoticeDTO() {
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

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public List<Long> getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(List<Long> recipientIds) {
        this.recipientIds = recipientIds;
    }
}