package com.chenailin.www.core.servlet;

import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * 所有Servlet的基类，提供通用功能
 * @author evi
 */
public abstract class BaseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseServlet.class);

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("{} initialized", getClass().getSimpleName());
    }

    /**
     * 获取用户ID，如果用户未登录则返回null
     */
    protected Long getUserId(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            if (userId instanceof Long) {
                return (Long) userId;
            }
        }
        return null;
    }

    /**
     * 解析请求路径，去除上下文路径
     */
    protected String getRequestPath(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }

    /**
     * 从请求体解析JSON对象
     */
    protected <T> T parseRequestBody(HttpServletRequest req, Class<T> clazz) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }
        return JsonUtil.fromJson(requestBody.toString(), clazz);
    }

    /**
     * 发送JSON响应
     */
    protected void sendJsonResponse(HttpServletResponse resp, ResultVO<?> result) throws IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(result));
    }

    /**
     * 发送错误响应
     */
    protected void sendError(HttpServletResponse resp, int code, String message) throws IOException {
        resp.setStatus(code);
        resp.setContentType("application/json;charset=UTF-8");
        resp.getWriter().write(JsonUtil.toJson(ResultVO.error(code, message)));
    }

    /**
     * 检查用户是否已登录，如果未登录则发送401错误
     * @return 如果用户已登录则返回true，否则返回false
     */
    protected boolean checkAuthentication(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Long userId = getUserId(req);
        if (userId == null) {
            sendError(resp, HttpServletResponse.SC_UNAUTHORIZED, "用户未登录或会话已过期");
            return false;
        }
        return true;
    }
}