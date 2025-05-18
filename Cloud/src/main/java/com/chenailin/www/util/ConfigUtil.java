package com.chenailin.www.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author evi
 */
public class ConfigUtil {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream is = ConfigUtil.class.getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (is != null) {
                PROPS.load(is);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return PROPS.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return PROPS.getProperty(key, defaultValue);
    }
}