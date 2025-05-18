package com.chenailin.www.exception;

/**
 * @author evi
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
