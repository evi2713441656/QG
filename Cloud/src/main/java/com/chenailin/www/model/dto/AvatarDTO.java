package com.chenailin.www.model.dto;

/**
 * @author evi
 */
public class AvatarDTO {
    private String avatarUrl;
    private String email;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public AvatarDTO() {
    }

    public AvatarDTO(String avatarUrl, String email) {
        this.avatarUrl = avatarUrl;
        this.email = email;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
