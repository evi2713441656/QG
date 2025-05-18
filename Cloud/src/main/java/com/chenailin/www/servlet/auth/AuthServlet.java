package com.chenailin.www.servlet.auth;

import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.LoginDTO;
import com.chenailin.www.model.dto.RegisterDTO;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.vo.ResultVO;
import com.chenailin.www.model.vo.UserVO;
import com.chenailin.www.util.JsonUtil;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

/**
 * @author evi
 */
@WebServlet("/*")
public class AuthServlet extends BaseAuthServlet {
    private static final Logger logger = LoggerFactory.getLogger(AuthServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");
        String path = getRequestPath(req);
        if (path.endsWith(".html")) {
            // 考虑上下文路径
            String contextPath = req.getContextPath();
            String forwardPath = contextPath + path;

            try {
                // 获取默认的 RequestDispatcher
                javax.servlet.RequestDispatcher dispatcher = req.getServletContext().getNamedDispatcher("default");
                if (dispatcher != null) {
                    // 直接使用 RequestDispatcher 进行转发
                    dispatcher.forward(req, resp);
                } else {
                    logger.error("默认 RequestDispatcher 未找到");
                    sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "默认 RequestDispatcher 未找到");
                }
            } catch (Exception e) {
                logger.error("请求转发出错，路径: {}, 错误信息: {}", forwardPath, e.getMessage(), e);
                sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "请求转发出错");
            }
        } else {
            sendError(resp, HttpServletResponse.SC_NOT_FOUND, "接口不存在");
        }

        try {
            if ("/register".equals(path)) {
                handleRegister(req, resp);
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(400, "Invalid ID format")));
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(JsonUtil.toJson(ResultVO.error(500, e.getMessage())));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        resp.setHeader("X-Content-Type-Options", "nosniff");

        try {
            String path = getRequestPath(req);
            switch (path) {
                case "/login":
                    handleLogin(req, resp);
                    break;
                case "/register":
                    handleRegister(req, resp);
                    break;
                case "/email-code":
                    handleSendRegisterCode(req, resp);
                    break;
                case "/check-username":
                    handleCheckUsername(req, resp);
                    break;
                default:
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "接口不存在");
                    break;
            }

        } catch (BusinessException e) {
            logger.warn("业务异常: {}", e.getMessage());
            sendJsonResponse(resp, ResultVO.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("系统异常", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    //=== 核心处理方法 ===//
    private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            // 1. 解析请求体并记录日志
            LoginDTO loginDTO = parseRequestBody(req, LoginDTO.class);
            System.out.println("接收到登录请求，数据: {}"+ loginDTO);

            // 2. 验证 DTO
            validateLoginDTO(loginDTO);

            // 3. 验证码校验
            if (!authService.validateCaptcha(req, loginDTO.getCaptcha())) {
                sendError(resp, 400, "图形验证码错误");
                return;
            }

            // 4. 执行登录
            User user = authService.login(loginDTO, req);

            // 5. 创建会话
            createUserSession(req, user);

            // 6. 返回响应
            sendJsonResponse(resp, ResultVO.success(convertToUserVO(user)));
            logger.info("用户 {} 登录成功", loginDTO.getUsername());

        } catch (BusinessException e) {
            logger.error("登录业务异常: {}", e.getMessage(), e);
            sendJsonResponse(resp, ResultVO.error(e.getMessage()));
        } catch (Exception e) {
            System.out.println("【ERROR】登录处理异常: " + e.getMessage());
            logger.error("登录系统异常: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RegisterDTO registerDTO = parseRequestBody(req, RegisterDTO.class);

            // 验证DTO
            authService.validateRegisterDTO(registerDTO);

            // 验证邮箱验证码
            if (!verificationCodeService.validateCode(registerDTO.getEmail(), registerDTO.getEmailCode())) {
                sendError(resp, 400, "邮箱验证码错误或已过期");
                return;
            }
            // 执行注册
            authService.register(registerDTO, req);
            // 注册成功后返回响应
            sendJsonResponse(resp, ResultVO.success("注册成功"));

        } catch (BusinessException e) {
            logger.error("注册业务异常: {}", e.getMessage(), e);
            sendJsonResponse(resp, ResultVO.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("注册系统异常: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    /**
     * 处理发送注册验证码请求
     */
    private void handleSendRegisterCode(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. 从请求体解析 JSON
        try (BufferedReader reader = req.getReader()) {
            String requestBody = IOUtils.toString(reader);
            Map<String, String> params = JsonUtil.fromJson(requestBody, Map.class);
            String email = params.get("email");

            logger.info("获取到的邮箱地址: {}", email);
            if (email == null || email.isEmpty()) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "邮箱不能为空");
                return;
            }

            logger.info("开始发送验证码到邮箱: {}", email);
            // 2. 发送验证码
            verificationCodeService.sendVerificationCode(email);
            sendJsonResponse(resp, ResultVO.success("验证码发送成功"));
            logger.info("成功发送验证码到邮箱: {}", email);
        } catch (BusinessException e) {
            logger.error("发送验证码时业务异常: {}", e.getMessage(), e);
            sendJsonResponse(resp, ResultVO.error(e.getMessage()));
        } catch (IOException e) {
            logger.error("读取请求体或发送响应时出现 I/O 异常: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "读取或发送数据时出错");
        } catch (Exception e) {
            logger.error("发送验证码时出现未知异常: {}", e.getMessage(), e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "系统繁忙");
        }
    }

    /**
     * 处理用户名检查请求
     */
    private void handleCheckUsername(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 1. 获取用户名参数
        String username = req.getParameter("username");

        // 2. 验证参数
        if (username == null || username.trim().isEmpty()) {
            sendJsonResponse(resp, ResultVO.error("用户名不能为空"));
            return;
        }

        if (username.length() < 4) {
            sendJsonResponse(resp, ResultVO.error("用户名至少需要4个字符"));
            return;
        }

        // 检查用户名是否可用
        try {
            boolean isAvailable = authService.isUsernameAvailable(username);
            if (isAvailable) {
                sendJsonResponse(resp, ResultVO.success("用户名可用"));
            } else {
                sendJsonResponse(resp, ResultVO.error("用户名已被占用"));
            }
        } catch (Exception e) {
            logger.error("检查用户名时出错", e);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "检查用户名失败");
        }
    }

    private void validateLoginDTO(LoginDTO dto) throws BusinessException {
        if (dto.getUsername().isEmpty() || dto.getPassword().isEmpty()) {
            throw new BusinessException("用户名不能为空");
        }
    }

    private UserVO convertToUserVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setPassword(user.getPassword());
        vo.setAvatar(user.getAvatar());
        // 不返回敏感字段
        return vo;
    }
}