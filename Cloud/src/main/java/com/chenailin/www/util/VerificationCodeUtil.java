package com.chenailin.www.util;

import java.util.Random;

/**
 * @author evi
 */
public class VerificationCodeUtil {
    /**
     * 生成6位数字验证码
     */
    public static String generateCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }
}
