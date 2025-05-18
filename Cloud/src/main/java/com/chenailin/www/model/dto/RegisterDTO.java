package com.chenailin.www.model.dto;

/**
 * 用户注册数据传输对象
 * @author evi
 */
public class RegisterDTO {
//    @NotBlank(message = "用户名不能为空")
//    @Size(min = 4, max = 20, message = "用户名长度必须在4-20个字符之间")
//    @Pattern(regexp = "^[a-zA-Z0-9_\\u4e00-\\u9fa5]+$",
//            message = "用户名只能包含中文、字母、数字和下划线")
    private String username;

//    @NotBlank(message = "密码不能为空")
//    @Size(min = 8, max = 20, message = "密码长度必须在8-20个字符之间")
//    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$",
//            message = "密码必须包含大小写字母、数字和特殊字符")
    private String password;

//    @NotBlank(message = "邮箱不能为空")
//    @Email(message = "邮箱格式不正确")
    private String email;

//    @NotBlank(message = "验证码不能为空")
//    @Size(min = 6, max = 6, message = "验证码必须是6位数字")
//    @Pattern(regexp = "^\\d{6}$", message = "验证码必须是数字")
    private String emailCode;

    // 图片验证码相关字段
//    @NotBlank(message = "图片验证码不能为空")
//    private String captcha;
//
//    private String captchaToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

//    public String getCaptcha() {
//        return captcha;
//    }
//
//    public void setCaptcha(String captcha) {
//        this.captcha = captcha;
//    }
//
//    public String getCaptchaToken() {
//        return captchaToken;
//    }
//
//    public void setCaptchaToken(String captchaToken) {
//        this.captchaToken = captchaToken;
//    }

    @Override
    public String toString() {
        return "RegisterDTO{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", emailCode='***'}";
    }
}