package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.UserDao;
import com.chenailin.www.dao.UserRelationDao;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.daoimpl.UserRelationDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.AvatarDTO;
import com.chenailin.www.model.dto.UserDTO;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.vo.UserVO;
import com.chenailin.www.service.UserService;

/**
 * @author evi
 */
public class UserServiceImpl implements UserService {
    private final UserDao userDao = new UserDaoImpl();
    private final UserRelationDao userRelationDao = new UserRelationDaoImpl();

    @Override
    public UserVO getUserById(Long id, Long currentUserId) {
        // 验证用户是否存在
        User user = userDao.findById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取关注数和粉丝数
        int followingCount = userRelationDao.countFollowing(id);
        int followerCount = userRelationDao.countFollowers(id);

        // 检查当前用户是否关注该用户
        boolean isFollowed = false;
        if (currentUserId != null && !currentUserId.equals(id)) {
            isFollowed = userRelationDao.isFollowing(currentUserId, id);
        }

        // 转换为VO
        return UserVO.fromEntityWithRelation(user, followingCount, followerCount, isFollowed);
    }

    @Override
    public void updateUserInfo(User user) {
        // 验证用户是否存在
        User existingUser = userDao.findById(user.getId());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 如果修改了用户名，检查是否可用
        if (!existingUser.getUsername().equals(user.getUsername()) && !userDao.checkUsernameAvailable(user.getUsername())) {
            throw new BusinessException("用户名已存在");
        }

        // 如果修改了邮箱，检查是否可用
        if (!existingUser.getEmail().equals(user.getEmail()) && !userDao.checkEmailAvailable(user.getEmail())) {
            throw new BusinessException("邮箱已被注册");
        }

        // 保持密码不变，除非明确要修改密码
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            user.setPassword(existingUser.getPassword());
        }

        // 更新用户信息
        userDao.update(user);
    }

    @Override
    public void saveUserInfo(UserDTO userDTO) {
        // 验证用户是否存在
        User existingUser = userDao.findByEmail(userDTO.getOldEmail());
        if (existingUser == null) {
            throw new BusinessException("用户不存在");
        }
        userDao.saveInfo(userDTO, existingUser.getId());
    }

    @Override
    public void updateAvatar(AvatarDTO avatarDTO) {
        // 验证用户是否存在
        User user = userDao.findByEmail(avatarDTO.getEmail());
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新头像
        userDao.updateAvatar(user.getId(), avatarDTO.getAvatarUrl());
    }

//    @Override
//    public List<UserVO> searchUsers(String keyword, int page, int size, Long currentUserId) {
//        int offset = (page - 1) * size;
//        List<User> users = userDao.searchByKeyword(keyword, size, offset);
//
//        // 转换为VO
//        return users.stream()
//                .map(user -> {
//                    int followingCount = userRelationDao.countFollowing(user.getId());
//                    int followerCount = userRelationDao.countFollowers(user.getId());
//                    boolean isFollowed = false;
//                    if (currentUserId != null && !currentUserId.equals(user.getId())) {
//                        isFollowed = userRelationDao.isFollowing(currentUserId, user.getId());
//                    }
//                    return UserVO.fromEntityWithRelation(user, followingCount, followerCount, isFollowed);
//                })
//                .collect(Collectors.toList());
//    }

    @Override
    public boolean isUsernameAvailable(String username) {
        return userDao.checkUsernameAvailable(username);
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return userDao.checkEmailAvailable(email);
    }
}