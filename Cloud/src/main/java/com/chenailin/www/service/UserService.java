package com.chenailin.www.service;

import com.chenailin.www.model.dto.AvatarDTO;
import com.chenailin.www.model.dto.UserDTO;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.vo.UserVO;

/**
 * @author evi
 */
public interface UserService {

    UserVO getUserById(Long id, Long currentUserId);

    void updateUserInfo(User user);

    void saveUserInfo(UserDTO userDTO);

    void updateAvatar(AvatarDTO avatarDTO);

//    List<UserVO> searchUsers(String keyword, int page, int size, Long currentUserId);

    boolean isUsernameAvailable(String username);

    boolean isEmailAvailable(String email);
}