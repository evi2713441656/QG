package com.chenailin.www.util;

import org.mindrot.jbcrypt.BCrypt;

/**
 * @author evi
 */
public class PasswordUtil {
    private static final int LOG_ROUNDS = 12;

    public static String encode(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(LOG_ROUNDS));
    }

    public static boolean matches(String rawPassword, String encodedPassword) {
        if (encodedPassword == null || encodedPassword.isEmpty()) {
            return false;
        }
        return BCrypt.checkpw(rawPassword, encodedPassword);
    }
}