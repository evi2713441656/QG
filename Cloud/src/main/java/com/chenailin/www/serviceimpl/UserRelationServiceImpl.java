package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.UserDao;
import com.chenailin.www.dao.UserRelationDao;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.daoimpl.UserRelationDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.pojo.UserRelation;
import com.chenailin.www.model.vo.UserVO;
import com.chenailin.www.service.UserRelationService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author evi
 */
public class UserRelationServiceImpl implements UserRelationService {
    private final UserRelationDao userRelationDao = new UserRelationDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public void follow(Long followerId, Long followingId) {
        // 不能关注自己
        if (followerId.equals(followingId)) {
            throw new BusinessException("不能关注自己");
        }

        // 验证被关注用户存在
        User followingUser = userDao.findById(followingId);
        if (followingUser == null) {
            throw new BusinessException("用户不存在");
        }

        // 检查是否已关注
        if (userRelationDao.isFollowing(followerId, followingId)) {
            throw new BusinessException("已经关注该用户");
        }

        // 创建关注关系
        UserRelation relation = new UserRelation();
        relation.setFollowerId(followerId);
        relation.setFollowingId(followingId);
        relation.setCreateTime(new Date());

        userRelationDao.save(relation);
    }

    @Override
    public void unfollow(Long followerId, Long followingId) {
        // 检查是否已关注
        if (!userRelationDao.isFollowing(followerId, followingId)) {
            throw new BusinessException("未关注该用户");
        }

        // 删除关注关系
        userRelationDao.delete(followerId, followingId);
    }

    @Override
    public List<UserVO> getFollowing(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<UserRelation> relations = userRelationDao.findFollowing(userId, size, offset);

        // 转换为UserVO
        List<UserVO> result = new ArrayList<>();
        for (UserRelation relation : relations) {
            User user = userDao.findById(relation.getFollowingId());
            if (user != null) {
                result.add(convertToVO(user));
            }
        }

        return result;
    }

    @Override
    public List<UserVO> getFollowers(Long userId, int page, int size) {
        int offset = (page - 1) * size;
        List<UserRelation> relations = userRelationDao.findFollowers(userId, size, offset);

        // 转换为UserVO
        List<UserVO> result = new ArrayList<>();
        for (UserRelation relation : relations) {
            User user = userDao.findById(relation.getFollowerId());
            if (user != null) {
                result.add(convertToVO(user));
            }
        }

        return result;
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        return userRelationDao.isFollowing(followerId, followingId);
    }

    @Override
    public int countFollowing(Long userId) {
        return userRelationDao.countFollowing(userId);
    }

    @Override
    public int countFollowers(Long userId) {
        return userRelationDao.countFollowers(userId);
    }

    @Override
    public List<UserVO> getCommonFollowing(Long userId1, Long userId2, int limit) {
        List<Long> commonIds = userRelationDao.findCommonFollowing(userId1, userId2, limit);

        // 转换为UserVO
        List<UserVO> result = new ArrayList<>();
        for (Long id : commonIds) {
            User user = userDao.findById(id);
            if (user != null) {
                result.add(convertToVO(user));
            }
        }

        return result;
    }

    private UserVO convertToVO(User user) {
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setAvatar(user.getAvatar());

        // 设置关注和粉丝数量
        vo.setFollowingCount(userRelationDao.countFollowing(user.getId()));
        vo.setFollowerCount(userRelationDao.countFollowers(user.getId()));

        return vo;
    }
}