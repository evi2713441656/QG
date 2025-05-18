package com.chenailin.www.dao;

import com.chenailin.www.model.dto.UserDTO;
import com.chenailin.www.model.pojo.User;

/**
 * @author evi
 */
public interface UserDao {

    User findByUsername(String username);

    User findByEmail(String email);

    User findById(Long id);

    void save(User user);

    void saveInfo(UserDTO userDTO, Long id);

    void update(User user);

    void updatePassword(Long userId, String newPassword);

//    List<User> searchByKeyword(String keyword, int limit, int offset);

    int countTotal();

    void updateAvatar(Long userId, String avatarUrl);

    boolean checkUsernameAvailable(String username);

    boolean checkEmailAvailable(String email);
}