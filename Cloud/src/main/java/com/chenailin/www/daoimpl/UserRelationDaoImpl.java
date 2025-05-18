package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.UserRelationDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.UserRelation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author evi
 */
public class UserRelationDaoImpl extends BaseDao<UserRelation> implements UserRelationDao {
    private static final Logger logger = LoggerFactory.getLogger(UserRelationDaoImpl.class);

    @Override
    protected UserRelation mapResultSet(ResultSet rs) throws SQLException {
        UserRelation relation = new UserRelation();
        relation.setId(rs.getLong("id"));
        relation.setFollowerId(rs.getLong("follower_id"));
        relation.setFollowingId(rs.getLong("following_id"));
        relation.setCreateTime(rs.getTimestamp("create_time"));
        return relation;
    }

    @Override
    public List<UserRelation> findFollowing(Long followerId, int limit, int offset) {
        String sql = "SELECT * FROM user_relation WHERE follower_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, followerId, limit, offset);
    }

    @Override
    public List<UserRelation> findFollowers(Long followingId, int limit, int offset) {
        String sql = "SELECT * FROM user_relation WHERE following_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, followingId, limit, offset);
    }

    @Override
    public boolean isFollowing(Long followerId, Long followingId) {
        String sql = "SELECT COUNT(*) FROM user_relation WHERE follower_id = ? AND following_id = ?";
        return executeCountQuery(sql, followerId, followingId) > 0;
    }

    @Override
    public void save(UserRelation relation) {
        String sql = "INSERT INTO user_relation (follower_id, following_id, create_time) VALUES (?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, relation.getFollowerId(), relation.getFollowingId(), new java.sql.Timestamp(relation.getCreateTime().getTime()));
        relation.setId(id);
    }

    @Override
    public void delete(Long followerId, Long followingId) {
        String sql = "DELETE FROM user_relation WHERE follower_id = ? AND following_id = ?";
        executeUpdate(sql, followerId, followingId);
    }

    @Override
    public int countFollowing(Long followerId) {
        String sql = "SELECT COUNT(*) FROM user_relation WHERE follower_id = ?";
        return executeCountQuery(sql, followerId);
    }

    @Override
    public int countFollowers(Long followingId) {
        String sql = "SELECT COUNT(*) FROM user_relation WHERE following_id = ?";
        return executeCountQuery(sql, followingId);
    }

    @Override
    public List<Long> findCommonFollowing(Long userId1, Long userId2, int limit) {
        String sql = "SELECT r1.following_id FROM user_relation r1 " +
                "JOIN user_relation r2 ON r1.following_id = r2.following_id " +
                "WHERE r1.follower_id = ? AND r2.follower_id = ? " +
                "LIMIT ?";
        // 这里需要特殊处理，因为返回的是 Long 列表，而不是 UserRelation 列表
        List<Long> result = new ArrayList<>();
        try (java.sql.Connection conn = com.chenailin.www.util.DBUtil.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, userId1);
            stmt.setLong(2, userId2);
            stmt.setInt(3, limit);
            java.sql.ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(rs.getLong("following_id"));
            }
        } catch (java.sql.SQLException e) {
            logger.error("查询共同关注失败, userId1: " + userId1 + ", userId2: " + userId2, e);
            throw new DataAccessException("查询共同关注失败", e);
        }
        return result;
    }
}