package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.UserDao;
import com.chenailin.www.model.dto.UserDTO;
import com.chenailin.www.model.pojo.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author evi
 */
public class UserDaoImpl extends BaseDao<User> implements UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDaoImpl.class);

    @Override
    protected User mapResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getLong("id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setEmail(rs.getString("email"));
        user.setAvatar(rs.getString("avatar"));
        user.setStatus(rs.getInt("status"));
        user.setLastLoginTime(new Date(rs.getTimestamp("last_login_time").getTime()));
        user.setCreateTime(new Date(rs.getTimestamp("create_time").getTime()));
        user.setUpdateTime(new Date(rs.getTimestamp("update_time").getTime()));
        return user;
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM user WHERE username = ?";
        return executeQuerySingle(sql, username);
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        return executeQuerySingle(sql, email);
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO user (username, email, password, avatar, status, last_login_time, create_time, update_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getAvatar(), user.getStatus(), new java.sql.Timestamp(user.getLastLoginTime().getTime()), new java.sql.Timestamp(user.getCreateTime().getTime()), new java.sql.Timestamp(user.getUpdateTime().getTime()));
        user.setId(id);
    }

    @Override
    public void saveInfo(UserDTO userDTO, Long id) {
        String sql = "UPDATE user SET username = ?, email = ? WHERE id = ?";
        executeUpdate(sql, userDTO.getUsername(), userDTO.getEmail(), id);
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE user SET username = ?, email = ?, password = ?, avatar = ?, status = ?, last_login_time = ?, update_time = ? WHERE id = ?";
        executeUpdate(sql, user.getUsername(), user.getEmail(), user.getPassword(), user.getAvatar(), user.getStatus(), new java.sql.Timestamp(user.getLastLoginTime().getTime()), new java.sql.Timestamp(user.getUpdateTime().getTime()), user.getId());
    }

    @Override
    public void updatePassword(Long userId, String newPassword) {
        String sql = "UPDATE user SET password = ? WHERE id = ?";
        executeUpdate(sql, newPassword, userId);
    }

//    @Override
//    public List<User> searchByKeyword(String keyword, int limit, int offset) {
//        String sql = "SELECT * FROM user WHERE username LIKE ? OR email LIKE ? LIMIT ? OFFSET ?";
//        String likePattern = "%" + keyword + "%";
//        return executeQuery(sql, likePattern, likePattern, limit, offset);
//    }

    @Override
    public int countTotal() {
        String sql = "SELECT COUNT(*) FROM user";
        return executeCountQuery(sql);
    }

    @Override
    public void updateAvatar(Long userId, String avatarUrl) {
        String sql = "UPDATE user SET avatar = ? WHERE id = ?";
        executeUpdate(sql, avatarUrl, userId);
    }

    @Override
    public boolean checkUsernameAvailable(String username) {
        String sql = "SELECT COUNT(*) FROM user WHERE username = ?";
        return executeCountQuery(sql, username) == 0;
    }

    @Override
    public boolean checkEmailAvailable(String email) {
        String sql = "SELECT COUNT(*) FROM user WHERE email = ?";
        return executeCountQuery(sql, email) == 0;
    }
}