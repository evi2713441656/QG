package com.chenailin.www.servlet.auth;

import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author evi
 */
@WebServlet("/email-code")
public class EmailServlet extends BaseAuthServlet {
    private static final Logger logger = LoggerFactory.getLogger(EmailServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        try {
            // 1. 读取并解析JSON请求体
            BufferedReader reader = req.getReader();
            Map<String, String> params = JsonUtil.fromJson(IOUtils.toString(reader), Map.class);
            String email = params.get("email");

            // 3. 发送验证码
            logger.info("准备发送验证码到邮箱: {}", email);
            verificationCodeService.sendVerificationCode(email);

            // 4. 返回成功响应 ???
            sendJsonResponse(resp, HttpServletResponse.SC_ACCEPTED,"验证码发送成功");
            logger.info("验证码发送成功: {}", email);

        } catch (BusinessException e) {
            logger.warn("业务异常: {}", e.getMessage());
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            logger.error("系统异常", e);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");

        resp.getWriter().write(JsonUtil.toJson(ResultVO.error(405, "请使用POST方法请求此接口")));

    }

    /**
     * 发送JSON格式响应
     */
    private void sendJsonResponse(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("code", statusCode);
        responseMap.put("message", message);
        resp.getWriter().write(JsonUtil.toJson(responseMap));
    }
}