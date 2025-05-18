package com.chenailin.www.servlet.auth;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.core.servlet.BaseServlet;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.service.AuthService;
import com.chenailin.www.service.VerificationCodeService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 认证相关Servlet的基类
 * @author evi
 */
public abstract class BaseAuthServlet extends BaseServlet {

    protected AuthService authService;
    protected VerificationCodeService verificationCodeService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.authService = (AuthService) container.getBean("authService");
        this.verificationCodeService = (VerificationCodeService) container.getBean("verificationCodeService");
    }

    /**
     * 创建用户会话
     * @param req HttpServletRequest
     * @param user 用户对象
     */
    protected void createUserSession(HttpServletRequest req, User user) {
        HttpSession session = req.getSession();
        session.setAttribute("currentUser", user);
        session.setAttribute("username", user.getUsername());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("password", user.getPassword());
        session.setAttribute("avatar", user.getAvatar());
        session.setAttribute("userId", user.getId());
        // 30分钟过期
        session.setMaxInactiveInterval(30 * 60);
    }
}