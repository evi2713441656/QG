package com.chenailin.www.model.dto;

/**
 * @author evi
 */
public class LoginDTO {
    private String username;
    private String password;
    // 图片验证码
    private String captcha;
    private String captchaToken;

    public String getCaptchaToken() {
        return captchaToken;
    }

    public void setCaptchaToken(String captchaToken) {
        this.captchaToken = captchaToken;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LoginDTO() {
    }

    public LoginDTO(String username, String password, String captcha, String captchaToken) {
        this.username = username;
        this.password = password;
        this.captcha = captcha;
        this.captchaToken = captchaToken;
    }
}