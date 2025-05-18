package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.UserDao;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.LoginDTO;
import com.chenailin.www.model.dto.RegisterDTO;
import com.chenailin.www.model.dto.ResetPasswordDTO;
import com.chenailin.www.model.dto.UpdatePasswordDTO;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.service.AuthService;
import com.chenailin.www.util.PasswordUtil;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;
import java.util.Date;
import java.util.Random;

/**
 * @author evi
 */
public class AuthServiceImpl implements AuthService {
    private final UserDao userDao = new UserDaoImpl();
    private static final int EMAIL_CODE_LENGTH = 6;

    @Override
    public User login(LoginDTO loginDTO, HttpServletRequest request) {
        // 1. 验证验证码（实际应从Session获取比对）
//        String sessionCaptcha = SessionUtil.getCaptcha(request, loginDTO.getCaptchaToken());

//        System.out.println(sessionCaptcha);

//        if (!loginDTO.getCaptcha().equalsIgnoreCase(sessionCaptcha)) {
//            System.out.println("wozaizhe111");
//            throw new BusinessException("验证码错误或已过期");
//        }
//        SessionUtil.removeCaptcha(request, loginDTO.getCaptchaToken());

        // 2. 查询用户
        User user = userDao.findByUsername(loginDTO.getUsername());
        if (user == null) {
            throw new RuntimeException("没有该账号信息");
        }

        // 3. 验证密码
        if (!PasswordUtil.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 检查用户状态
        if (user.getStatus() != 1) {
            throw new BusinessException("账号已被禁用，请联系管理员");
        }

        // 5. 更新最后登录时间
        user.setLastLoginTime(new Date());
        userDao.update(user);

        return user;
    }

    @Override
    public void register(RegisterDTO registerDTO, HttpServletRequest request) {
//        // 1. 验证邮箱验证码
//        String sessionCode = SessionUtil.getEmailCode(request, registerDTO.getEmail());
//        if (!registerDTO.getEmailCode().equals(sessionCode)) {
//            throw new BusinessException("邮箱验证码错误或已过期");
//        }
//        SessionUtil.removeEmailCode(request, registerDTO.getEmail());
//
//        // 2. 检查用户名是否已存在
//        if (checkUsernameExists(registerDTO.getUsername())) {
//            throw new BusinessException("用户名已存在");
//        }
//
//        // 3. 检查邮箱是否已存在
//        if (checkEmailExists(registerDTO.getEmail())) {
//            throw new BusinessException("邮箱已被注册");
//        }

        // 4. 创建用户
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encode(registerDTO.getPassword()));
        user.setEmail(registerDTO.getEmail());
        user.setAvatar("/default-avatar.png");
        user.setStatus(1);
        user.setLastLoginTime(new Date());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userDao.save(user);
    }

    @Override
    public void resetPassword(ResetPasswordDTO resetDTO, HttpServletRequest request) {

        // 2. 查询用户
        User user = userDao.findByEmail(resetDTO.getEmail());
        if (user == null) {
            throw new BusinessException("邮箱未注册");
        }

        // 3. 更新密码
        user.setPassword(PasswordUtil.encode(resetDTO.getNewPassword()));
        user.setUpdateTime(new Date());
        userDao.update(user);
    }

    @Override
    public void updatePassword(UpdatePasswordDTO updateDTO, HttpServletRequest request) {

        // 2. 查询用户
        User user = userDao.findByEmail(updateDTO.getEmail());
        if (user == null) {
            throw new BusinessException("邮箱未注册");
        }

        // 3. 更新密码
        user.setPassword(PasswordUtil.encode(updateDTO.getNewPassword()));
        user.setUpdateTime(new Date());
        userDao.update(user);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return userDao.findByUsername(username) != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return userDao.findByEmail(email) != null;
    }

    @Override
    public void validateRegisterDTO(RegisterDTO registerDTO) {
        if (registerDTO == null) {
            throw new IllegalArgumentException("注册信息不能为空");
        }

        // 验证用户名
        if (registerDTO.getUsername().isEmpty()) {
            throw new IllegalArgumentException("用户名不能为空");
        }
        if (registerDTO.getUsername().length() < 4 || registerDTO.getUsername().length() > 20) {
            throw new IllegalArgumentException("用户名长度必须在4-20个字符之间");
        }
        if (!registerDTO.getUsername().matches("^[a-zA-Z0-9_]+$")) {
            throw new IllegalArgumentException("用户名只能包含字母、数字和下划线");
        }

        // 验证密码
        if (registerDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("密码不能为空");
        }
        if (registerDTO.getPassword().length() < 8 || registerDTO.getPassword().length() > 32) {
            throw new IllegalArgumentException("密码长度必须在8-32个字符之间");
        }
        if (!registerDTO.getPassword().matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$")) {
            throw new IllegalArgumentException("密码必须包含大小写字母和数字");
        }

//        // 验证确认密码
//        if (!registerDTO.getPassword().equals(registerDTO.getConfirmPassword())) {
//            throw new IllegalArgumentException("两次输入的密码不一致");
//        }

        // 验证邮箱
        if (registerDTO.getEmail().isEmpty()) {
            throw new IllegalArgumentException("邮箱不能为空");
        }
        if (!registerDTO.getEmail().matches("^[\\w-]+(\\.[\\w-]+)*@[\\w-]+(\\.[\\w-]+)+$")) {
            throw new IllegalArgumentException("邮箱格式不正确");
        }

//        // 验证手机号（可选）
//        if (registerDTO.getPhone().isEmpty()) {
//            if (!registerDTO.getPhone().matches("^1[3-9]\\d{9}$")) {
//                throw new IllegalArgumentException("手机号格式不正确");
//            }
//        }
    }

    @Override
    public boolean validateCaptcha(HttpServletRequest req, String userInput) {
        String sessionCode = (String) req.getSession().getAttribute("captchaText");
        System.out.println(sessionCode);
        if (sessionCode == null) {
            return false;
        }

        // 验证后立即清除（确保一次性使用）
        req.getSession().removeAttribute("captchaText");

        // 不区分大小写比较
        return sessionCode.equalsIgnoreCase(userInput);
    }

    @Override
    public boolean isUsernameAvailable(String username) throws SQLException {
        // 1. 检查用户名长度等基本规则
        if (username == null || username.length() < 4) {
            return false;
        }
        // 2. 检查是否已存在
        return userDao.findByUsername(username)==null;
    }
}