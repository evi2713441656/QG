package com.chenailin.www.servlet.auth;

import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.chenailin.www.util.CaptchaUtil.generateDummyImageUrl;
import static com.chenailin.www.util.CaptchaUtil.generateRandomCode;

/**
 * @author evi
 */
@WebServlet("/captcha")
public class CaptchaServlet extends BaseAuthServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        // 1. 生成验证码（示例使用简单实现）
        String captchaText = generateRandomCode(4);
        String token = UUID.randomUUID().toString();

        // 2. 存储到Session（用于后续验证）
        req.getSession().setAttribute("captchaToken", token);
        req.getSession().setAttribute("captchaText", captchaText);

        // 3. 返回JSON响应
        resp.setContentType("application/json");
        Map<String, String> responseData = new HashMap<>();

        // 示例
        responseData.put("image", generateDummyImageUrl(captchaText));
        responseData.put("token", token);
        resp.getWriter().write(new Gson().toJson(responseData));
    }
}