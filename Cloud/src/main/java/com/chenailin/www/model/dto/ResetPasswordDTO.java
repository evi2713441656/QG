package com.chenailin.www.model.dto;

/**
 * @author evi
 */
public class ResetPasswordDTO {
    private String email;
    private String emailCode;
    private String newPassword;

    // 构造方法
    public ResetPasswordDTO() {}

    public ResetPasswordDTO(String email, String emailCode, String newPassword) {
        this.email = email;
        this.emailCode = emailCode;
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailCode() {
        return emailCode;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
