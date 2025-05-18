package com.chenailin.www.model.dto;

/**
 * Data transfer object for user message
 * @author evi
 */
public class UserMessageDTO {
    private Long id;
    private Long recipientId;
    private String content;

    public UserMessageDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}