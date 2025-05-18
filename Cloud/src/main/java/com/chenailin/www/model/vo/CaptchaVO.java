package com.chenailin.www.model.vo;

/**
 * @author evi
 */
public class CaptchaVO {

    // 验证码文本（服务端使用）
    private String code;
    // Base64图片（前端显示）
    private String image;
    // 验证码令牌（用于关联Session）
    private String token;

    public CaptchaVO() {}

    public CaptchaVO(String code, String image) {
        this.code = code;
        this.image = image;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}