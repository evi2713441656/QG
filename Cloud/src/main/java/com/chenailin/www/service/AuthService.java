package com.chenailin.www.service;

import com.chenailin.www.model.dto.LoginDTO;
import com.chenailin.www.model.dto.RegisterDTO;
import com.chenailin.www.model.dto.ResetPasswordDTO;
import com.chenailin.www.model.dto.UpdatePasswordDTO;
import com.chenailin.www.model.pojo.User;

import javax.servlet.http.HttpServletRequest;
import java.sql.SQLException;

/**
 * @author evi
 */
public interface AuthService {

    User login(LoginDTO loginDTO, HttpServletRequest request);

    void register(RegisterDTO registerDTO, HttpServletRequest request);

    void resetPassword(ResetPasswordDTO resetDTO, HttpServletRequest request);

    void updatePassword(UpdatePasswordDTO updateDTO, HttpServletRequest request);

    boolean checkUsernameExists(String username);

    boolean checkEmailExists(String email);

    void validateRegisterDTO(RegisterDTO registerDTO);

    boolean validateCaptcha(HttpServletRequest req, String userInput);

    boolean isUsernameAvailable(String username) throws SQLException;
}
