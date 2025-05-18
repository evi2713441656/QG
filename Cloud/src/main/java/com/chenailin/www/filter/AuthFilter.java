package com.chenailin.www.filter;

import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * @author evi
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Set<String> EXACT_WHITE_LIST = new HashSet<>(Arrays.asList(
            "/login", "/register", "/captcha", "/email-code",
            "/login.html", "/register.html", "/check-username", "/knowledge.html", "/getuserinfo", "/saveuserinfo",
            "/knowledge", "/knowledge/article", "/knowledge/article/detail", "/knowledge/list/my", "/knowledge/",
            "/save-article", "/forgot-password.html", "/reset-code", "/reset-password", "/update-password",
            "/browse-history", "/enterprise"
    ));
    private static final List<String> PREFIX_WHITE_LIST = Arrays.asList(
            "/static/", "/public/", "/assets/", "/css/", "/js/", "/captcha", "/knowledge", "/article", "/knowledge/",
            "/article/", "/comment", "/comment/", "/getuserinfo/", "/modify/", "/browse-history/", "/search/", "/enterprise/"
    );

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse)resp;
        String path = normalizePath(request);

        System.out.println("请求URI: " + request.getRequestURI());
//        System.out.println("上下文路径: " + request.getContextPath());
//        System.out.println("Servlet路径: " + request.getServletPath());
//        System.out.println("路径信息: " + request.getPathInfo());
        System.out.println("规范化路径: " + path);
        System.out.println("请求方法: " + request.getMethod());
//
//        System.out.println("路径"+path);
//        System.out.println("请求方法: " + ((HttpServletRequest)req).getMethod() + "，请求路径: " + ((HttpServletRequest)req).getRequestURI());
        if (isWhiteList(request, path)) {
            chain.doFilter(req, resp);
            return;
        }

        if (!checkAuthentication(request)) {
            sendUnauthorizedResponse(request, response, path);
            return;
        }

        chain.doFilter(req, resp);
    }

    /**
     * 解析请求路径，去除上下文路径
     */
    protected String getRequestPath(HttpServletRequest req) {
        return req.getRequestURI().substring(req.getContextPath().length());
    }

    private String normalizePath(HttpServletRequest req) {
        return (req.getServletPath() + (req.getPathInfo() != null ? req.getPathInfo() : ""))
                .toLowerCase()
                .replaceAll("/{2,}", "/");
    }

    private boolean isWhiteList(HttpServletRequest req, String path) {
        String contextPath = req.getContextPath();
        return EXACT_WHITE_LIST.contains(path) ||
                EXACT_WHITE_LIST.contains(contextPath + path) ||
                PREFIX_WHITE_LIST.stream().anyMatch(prefix ->
                        path.startsWith(prefix) ||
                                path.startsWith(contextPath + prefix));
    }

    // ...其他方法保持优化后的逻辑


    /**
     * 认证检查
     */
    private boolean checkAuthentication(HttpServletRequest req) {
        // 1. 检查Session
        HttpSession session = req.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }

    /**
     * 发送401响应
     */
    private void sendUnauthorizedResponse(HttpServletRequest req, HttpServletResponse resp, String path) throws IOException {
        // 获取上下文路径（如/Cloud）
        String contextPath = req.getContextPath();
        // 确保contextPath不以/结尾
        if (contextPath.endsWith("/")) {
            contextPath = contextPath.substring(0, contextPath.length() - 1);
        }
        // 1. 如果是API请求，返回JSON
        if (path.startsWith("/")) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write(JsonUtil.toJson(
                    ResultVO.error(401, "未登录或会话已过期，请重新登录11")
            ));
        }
        // 2. 否则重定向到登录页
        else {
            try {
                String redirectUrl = contextPath + "/login.html?redirect=" + URLEncoder.encode(path.startsWith("/") ? path : "/" + path, "UTF-8");
                resp.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
                resp.setHeader("Location", redirectUrl);
                System.out.println("Redirecting to: " + redirectUrl);
            } catch (UnsupportedEncodingException e) {
                System.err.println("Encoding error: " + e.getMessage());
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Encoding error");
            }
        }
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("AuthFilter 初始化开始");
        try {
            // 初始化代码（如数据库连接）
            System.out.println("AuthFilter 初始化成功");
        } catch (Exception e) {
            System.err.println("AuthFilter 初始化失败: " + e.getMessage());
            // 必须抛出
            throw new ServletException("Filter初始化失败", e);
        }
    }

    @Override
    public void destroy() {
        System.out.println("AuthFilter 销毁开始");
        try {
            // 注销所有JDBC驱动
            Enumeration<Driver> drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                if (driver.getClass().getClassLoader() == getClass().getClassLoader()) {
                    DriverManager.deregisterDriver(driver);
                }
            }
        } catch (SQLException e) {
            System.err.println("JDBC驱动注销失败: " + e.getMessage());
        }
    }
}