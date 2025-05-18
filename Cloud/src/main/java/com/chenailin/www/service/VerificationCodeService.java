package com.chenailin.www.service;

/**
 * @author evi
 */
public interface VerificationCodeService {

    public void sendVerificationCode(String email);

    public boolean validateCode(String email, String inputCode);
}