package com.chenailin.www.servlet.auth;
import com.chenailin.www.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author evi
 */
@WebServlet("/check-username")
public class UserServlet extends BaseAuthServlet {
    private static final Logger logger = LoggerFactory.getLogger(UserServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");

        try {
            String path = req.getRequestURI().substring(req.getContextPath().length());

            if ("/check-username".equals(path)) {
                handleCheckUsername(req, resp);
            } else {
                sendJsonResponse(resp, HttpServletResponse.SC_NOT_FOUND, "接口不存在");
            }
        } catch (Exception e) {
            logger.error("处理请求时出错", e);
            sendJsonResponse(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    /**
     * 处理用户名检查请求
     */
    private void handleCheckUsername(HttpServletRequest req, HttpServletResponse resp) throws IOException, SQLException {
        String username = req.getParameter("username");

        if (username==null || username.isEmpty()) {
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "用户名不能为空");
            return;
        }

        if (username.length() < 4) {
            sendJsonResponse(resp, HttpServletResponse.SC_BAD_REQUEST, "用户名至少需要4个字符");
            return;
        }
        boolean isAvailable = authService.isUsernameAvailable(username);
        if (isAvailable) {
            sendJsonResponse(resp, HttpServletResponse.SC_OK, "用户名可用");
        } else {
            sendJsonResponse(resp, HttpServletResponse.SC_CONFLICT, "用户名已被占用");
        }
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