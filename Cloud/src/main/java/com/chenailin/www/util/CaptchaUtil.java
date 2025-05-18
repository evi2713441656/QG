package com.chenailin.www.util;

import java.util.Random;

/**
 * @author evi
 */
public class CaptchaUtil {
    public static String generateRandomCode(int length) {
        // 简单随机字母数字生成
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public static String generateDummyImageUrl(String code) {
        // 临时方案：使用第三方验证码图片生成服务
        return "https://dummyimage.com/100x40/000/fff&text=" + code;
    }
}
