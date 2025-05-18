package com.chenailin.www.util;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.regex.Pattern;

/**
 * @author evi
 */
public class EmailUtil {
    private static final Properties PROPS = new Properties();
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    static {
        // 加载配置（从email.properties）
        // 强制指定SSL协议版本
        PROPS.put("mail.smtp.ssl.protocols", "TLSv1.2");
        PROPS.put("mail.smtp.host", ConfigUtil.get("email.smtp.host"));
        PROPS.put("mail.smtp.port", ConfigUtil.get("email.smtp.port"));
        PROPS.put("mail.smtp.ssl.enable", ConfigUtil.get("email.smtp.ssl.enable"));
        PROPS.put("mail.smtp.auth", ConfigUtil.get("email.smtp.auth"));
        PROPS.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        PROPS.put("mail.smtp.socketFactory.fallback", "false");
    }

    /**
     * 发送邮件（自动重试机制）
     */
    public static void sendEmail(String to, String subject, String content) {
        int maxRetries = 3;
        int retryCount = 0;

        while (retryCount < maxRetries) {
            try {
                doSendEmail(to, subject, content);
                return; // 发送成功则退出
            } catch (MessagingException e) {
                retryCount++;
                if (retryCount >= maxRetries) {
                    throw new RuntimeException("邮件发送失败（已尝试" + maxRetries + "次）", e);
                }
                try {
                    // 等待3秒后重试
                    Thread.sleep(3000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("邮件发送被中断", ie);
                }
            }
        }
    }

    /**
     * 实际发送邮件的核心方法
     */
    private static void doSendEmail(String to, String subject, String content) throws MessagingException {
        // 创建Session对象
        Session session = createSession();

        Message message = new MimeMessage(session);

        try {
            // 设置发件人（必须与认证用户一致）
            message.setFrom(new InternetAddress(ConfigUtil.get("email.from")));
//            System.out.println("成功设置发件人地址");

            // 设置收件人（支持多个收件人）
            InternetAddress[] recipients = {
                    new InternetAddress(to)
                    // 可以添加更多收件人
            };
            message.setRecipients(Message.RecipientType.TO, recipients);
//            System.out.println("成功设置收件人地址");

            // 设置邮件内容
            message.setSubject(subject);
            message.setContent(content, "text/html;charset=UTF-8");
//            System.out.println("成功设置邮件主题和内容");

            // 发送前验证
            validateMessage(message);
//            System.out.println("邮件验证通过");

            // 正式发送
            Transport.send(message);
//            System.out.println("邮件发送成功");
        } catch (AddressException e) {
            System.err.println("设置邮件地址环节失败，邮件地址格式错误");
            throw new MessagingException("邮件地址格式错误", e);
        } catch (MessagingException e) {
            System.err.println("邮件发送过程中出现异常，具体原因：" + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("邮件发送过程中出现未知异常，具体原因：" + e.getMessage());
            throw new MessagingException("邮件发送失败，出现未知异常", e);
        }
    }

    /**
     * 创建邮件会话（带超时设置）
     */
    private static Session createSession() {
        return Session.getInstance(PROPS, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        ConfigUtil.get("email.username"),
                        ConfigUtil.get("email.password")
                );
            }
        });
    }

    /**
     * 邮件发送前验证
     */
    private static void validateMessage(Message message) throws MessagingException {
        if (message.getFrom() == null || message.getFrom().length == 0) {
            throw new MessagingException("发件人地址未设置");
        }
        if (message.getAllRecipients() == null || message.getAllRecipients().length == 0) {
            throw new MessagingException("收件人地址未设置");
        }
        if (message.getSubject() == null || message.getSubject().isEmpty()) {
            throw new MessagingException("邮件主题不能为空");
        }
    }

    /**
     * 发送验证码（专用方法）
     */

    public static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
}