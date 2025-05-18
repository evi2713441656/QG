package com.chenailin.www.servlet.user;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.function.BiConsumer;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.model.dto.UserDTO;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.service.UserService;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * @author evi
 */
@WebServlet("/saveuserinfo")
public class SaveServlet extends HttpServlet {
    protected UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.userService = (UserService) container.getBean("userService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        // 封装通用的错误响应方法
        BiConsumer<Integer, String> sendError = (code, message) -> {
            resp.setStatus(code);
            String json = "{\"code\": " + code + ", \"message\": \"" + message + "\"}";
            out.print(json);
        };

        // 检查用户是否已登录
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            sendError.accept(HttpServletResponse.SC_UNAUTHORIZED, "用户未登录或会话已过期");
            return;
        }

        try {
            // 读取请求体
            StringBuilder requestBody = new StringBuilder();
            BufferedReader reader = req.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }

            // 解析 JSON 数据
            Gson gson = new Gson();
            UserDTO userDTO = gson.fromJson(requestBody.toString(), UserDTO.class);

            // 保存用户信息
            userService.saveUserInfo(userDTO);
            // 更新会话中的用户信息
            User currentUser = (User) session.getAttribute("currentUser");
            currentUser.setUsername(userDTO.getUsername());
            currentUser.setEmail(userDTO.getEmail());
            session.setAttribute("username", currentUser.getUsername());
            session.setAttribute("email", currentUser.getEmail());

            // 返回成功响应
            resp.setStatus(HttpServletResponse.SC_OK);
            String json = "{\"code\": 200, \"message\": \"保存成功\"}";
            out.print(json);

        } catch (JsonSyntaxException e) {
            // 处理JSON解析错误
            System.err.println("JSON解析错误: " + e.getMessage());
            sendError.accept(HttpServletResponse.SC_BAD_REQUEST, "请求格式错误");
        } catch (Exception e) {
            // 处理其他异常
            System.err.println("保存用户信息失败: " + e.getMessage());
            e.printStackTrace();
            sendError.accept(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器内部错误");
        } finally {
            out.flush();
            out.close();
        }
    }
}
