package com.chenailin.www.servlet.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * @author evi
 */
@WebServlet("/getuserinfo")
public class UserInfoServlet extends BaseUserServlet {
    // 模拟数据库存储用户信息
    private static final Map<String, Map<String, String>> USER_DATABASE = new HashMap<>();

    static {
        // 初始化一个示例用户
        Map<String, String> user = new HashMap<>();
        user.put("username", "testuser");
        user.put("email", "test@example.com");
        USER_DATABASE.put("testuser", user);
    }

    @Override
    public void init() throws ServletException {
        System.out.println("UserInfoController 初始化完成");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setHeader("X-Content-Type-Options", "nosniff");

        // 获取当前请求对应的会话
        HttpSession session = request.getSession(false);
        if (session == null) {
            // 会话不存在，返回 401 未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("没会话");
            return;
        }

        String username = (String) session.getAttribute("username");
        if (username == null) {
            // 会话中没有用户名，返回 401 未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("没用户名");
            return;
        }

        String email = (String) session.getAttribute("email");
        if (email == null) {
            // 会话中没有用户名，返回 401 未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("没邮箱");
            return;
        }

        String password = (String) session.getAttribute("password");
        if (password == null) {
            // 会话中没有用户名，返回 401 未授权错误
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("没密码");
            return;
        }

        String avatar = (String) session.getAttribute("avatar");
        if (avatar == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            System.out.println("没头像");
            return;
        }
        System.out.println(avatar);

        // 构造 JSON 响应 - 修复格式错误
        String jsonResponse = "{\"code\": 200, \"data\": {" +
                "\"username\": \"" + username + "\"," +
                "\"email\": \"" + email + "\"," +
                "\"password\": \"" + password + "\"," +
                "\"avatar\": \"" + avatar + "\"" +
                "}}";
        // 发送响应
        PrintWriter out = response.getWriter();
        out.print(jsonResponse);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = (String) request.getSession().getAttribute("username");
        Map<String, String> userInfo = USER_DATABASE.get(username);

        if (userInfo == null) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String newEmail = request.getParameter("email");
        if (newEmail != null && !newEmail.isEmpty()) {
            userInfo.put("email", newEmail);
        }

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.print("{\"message\": \"个人信息更新成功\"}");
        out.flush();
    }
}