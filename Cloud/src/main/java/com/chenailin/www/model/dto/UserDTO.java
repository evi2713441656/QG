package com.chenailin.www.model.dto;

/**
 * @author evi
 */
public class UserDTO {
    private String username;
    private String email;
    private String oldEmail;

    public String getOldEmail() {
        return oldEmail;
    }

    public UserDTO(String email, String oldEmail, String username) {
        this.email = email;
        this.oldEmail = oldEmail;
        this.username = username;
    }

    public void setOldEmail(String oldEmail) {
        this.oldEmail = oldEmail;
    }

    public UserDTO() {
    }

    public String getEmail() {
        return email;
    }

    public UserDTO(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
