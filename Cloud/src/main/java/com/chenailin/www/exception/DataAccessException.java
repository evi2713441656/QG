package com.chenailin.www.exception;

/**
 * @author evi
 */ // 自定义异常类
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
