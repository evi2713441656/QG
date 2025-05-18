package com.chenailin.www.servlet.auth;

import com.chenailin.www.model.dto.ResetPasswordDTO;
import com.chenailin.www.model.dto.UpdatePasswordDTO;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * @author evi
 */
@WebServlet(urlPatterns = {"/reset-code", "/reset-password", "/update-password"})
public class PasswordServlet extends BaseAuthServlet {
    Logger logger = LoggerFactory.getLogger(PasswordServlet.class);

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getRequestURI().substring(req.getContextPath().length());
        resp.setContentType("application/json;charset=UTF-8");
        try {
            switch (path) {
                case "/reset-code":
                    // 处理发送重置密码验证码请求
                    handleSendResetCode(req, resp);
                    break;
                case "/reset-password":
                    // 处理重置密码请求
                    handleResetPassword(req, resp);
                    break;
                case "/update-password":
                    // 处理重置密码请求
                    handleUpdatePassword(req, resp);
                    break;
                default:
                    // 无效的路径
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write(JsonUtil.toJson(ResultVO.error("接口不存在")));
                    break;
            }
        } catch (Exception e) {
            logger.error("处理重置密码请求出错", e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("服务器内部错误: " + e.getMessage())));
        }
    }

    /**
     * 处理发送重置密码验证码请求
     */
    private void handleSendResetCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 解析请求体
        String requestBody = IOUtils.toString(req.getReader());
        Map<String, String> params = JsonUtil.fromJson(requestBody, Map.class);
        String email = params.get("email");

        logger.info("发送重置密码验证码到邮箱: {}", email);

        if (email == null || email.isEmpty()) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("邮箱不能为空")));
            return;
        }

        try {
            // 发送验证码
            verificationCodeService.sendVerificationCode(email);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.success("验证码发送成功")));
        } catch (Exception e) {
            logger.error("发送重置密码验证码失败", e);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("发送验证码失败: " + e.getMessage())));
        }
    }

    /**
     * 处理重置密码请求
     */
    private void handleResetPassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 解析请求体
        String requestBody = IOUtils.toString(req.getReader());
        ResetPasswordDTO resetDTO = JsonUtil.fromJson(requestBody, ResetPasswordDTO.class);

        logger.info("重置密码请求，邮箱: {}", resetDTO.getEmail());

        // 验证请求参数
        if (resetDTO.getEmail() == null || resetDTO.getEmail().isEmpty()) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("邮箱不能为空")));
            return;
        }

        if (resetDTO.getEmailCode() == null || resetDTO.getEmailCode().isEmpty()) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("验证码不能为空")));
            return;
        }

        if (resetDTO.getNewPassword() == null || resetDTO.getNewPassword().isEmpty()) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("新密码不能为空")));
            return;
        }

        try {
            // 验证邮箱验证码
            if (!verificationCodeService.validateCode(resetDTO.getEmail(), resetDTO.getEmailCode())) {
                resp.getWriter().write(JsonUtil.toJson(ResultVO.error("验证码错误或已过期")));
                return;
            }

            // 执行重置密码
            authService.resetPassword(resetDTO, req);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.success("密码重置成功")));
        } catch (Exception e) {
            logger.error("重置密码失败", e);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("重置密码失败: " + e.getMessage())));
        }
    }

    private void handleUpdatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 解析请求体
        String requestBody = IOUtils.toString(req.getReader());
        UpdatePasswordDTO updateDTO = JsonUtil.fromJson(requestBody, UpdatePasswordDTO.class);

        if (updateDTO.getNewPassword() == null || updateDTO.getNewPassword().isEmpty()) {
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("新密码不能为空")));
            return;
        }

        try {
            // 执行重置密码
            authService.updatePassword(updateDTO, req);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.success("密码重置成功")));
        } catch (Exception e) {
            logger.error("重置密码失败", e);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error("重置密码失败: " + e.getMessage())));
        }
    }
}
