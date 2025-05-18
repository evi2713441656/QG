package com.chenailin.www.serviceimpl;

import com.chenailin.www.service.VerificationCodeService;
import com.chenailin.www.util.EmailUtil;
import com.chenailin.www.util.VerificationCodeUtil;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author evi
 */
public class VerificationCodeServiceImpl implements VerificationCodeService {
    // 使用内存存储验证码，实际项目应考虑持久化或分布式存储
    private static final Map<String, String> CODE_STORE = new ConcurrentHashMap<>();
    // 5分钟
    private static final long CODE_EXPIRE_TIME = 50 * 60 * 1000;

    @Override
    public void sendVerificationCode(String email) {
        String code = VerificationCodeUtil.generateCode();
        CODE_STORE.put(email, code);

        // 定时移除过期验证码
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                CODE_STORE.remove(email);
            }
        }, CODE_EXPIRE_TIME);

        // 发送邮件
        String subject = "您的注册验证码";
        String content = "您的注册验证码是：" + code + "，5分钟内有效";
        EmailUtil.sendEmail(email, subject, content);
    }

    @Override
    public boolean validateCode(String email, String inputCode) {
        String correctCode = CODE_STORE.get(email);
//        System.out.println("correctCode:"+correctCode);
        if (correctCode == null) {
            // 验证码不存在，可能已过期
//            System.out.println("correctCode为空");
            return false;
        }

        boolean isValid = inputCode != null && inputCode.equals(correctCode);
        if (isValid) {
            // 验证成功后移除验证码，确保验证码只能使用一次
            CODE_STORE.remove(email);
        }
        return isValid;
    }
}
