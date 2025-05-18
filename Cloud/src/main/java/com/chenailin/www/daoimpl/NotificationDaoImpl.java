package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.NotificationDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.Notification;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Implementation of NotificationDao
 * @author evi
 */
public class NotificationDaoImpl extends BaseDao<Notification> implements NotificationDao {

    @Override
    protected Notification mapResultSet(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getLong("id"));
        notification.setUserId(rs.getLong("user_id"));
        notification.setType(rs.getString("type"));
        notification.setContent(rs.getString("content"));
        notification.setRelatedId(rs.getLong("related_id"));
        if (rs.wasNull()) {
            notification.setRelatedId(null);
        }
        notification.setIsRead(rs.getBoolean("is_read"));
        notification.setReadTime(rs.getTimestamp("read_time"));
        notification.setCreateTime(rs.getTimestamp("create_time"));
        return notification;
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<Notification> findByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT * FROM notification WHERE user_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public List<Notification> findByUserIdAndType(Long userId, String type, int limit, int offset) {
        String sql = "SELECT * FROM notification WHERE user_id = ? AND type = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, type, limit, offset);
    }

    @Override
    public List<Notification> findUnreadByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT * FROM notification WHERE user_id = ? AND is_read = FALSE ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND is_read = FALSE";
        return executeCountQuery(sql, userId);
    }

    @Override
    public int countUnreadByUserIdAndType(Long userId, String type) {
        String sql = "SELECT COUNT(*) FROM notification WHERE user_id = ? AND type = ? AND is_read = FALSE";
        return executeCountQuery(sql, userId, type);
    }

    @Override
    public void save(Notification notification) {
        String sql = "INSERT INTO notification (user_id, type, content, related_id, is_read, read_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                notification.getUserId(),
                notification.getType(),
                notification.getContent(),
                notification.getRelatedId(),
                notification.isIsRead(),
                notification.getReadTime() != null ? new java.sql.Timestamp(notification.getReadTime().getTime()) : null,
                new java.sql.Timestamp(notification.getCreateTime().getTime()));

        if (id == null) {
            throw new DataAccessException("创建通知失败，无法获取ID");
        }
        notification.setId(id);
    }

    @Override
    public void update(Notification notification) {
        String sql = "UPDATE notification SET type = ?, content = ?, related_id = ?, is_read = ?, read_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                notification.getType(),
                notification.getContent(),
                notification.getRelatedId(),
                notification.isIsRead(),
                notification.getReadTime() != null ? new java.sql.Timestamp(notification.getReadTime().getTime()) : null,
                notification.getId());

        if (affectedRows == 0) {
            throw new DataAccessException("更新通知失败，没有行被影响");
        }
    }

    @Override
    public void markAsRead(Long id) {
        String sql = "UPDATE notification SET is_read = TRUE, read_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                id);

        if (affectedRows == 0) {
            throw new DataAccessException("标记通知已读失败，没有行被影响");
        }
    }

    @Override
    public void markAllAsRead(Long userId) {
        String sql = "UPDATE notification SET is_read = TRUE, read_time = ? WHERE user_id = ? AND is_read = FALSE";

        executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                userId);
    }

    @Override
    public void markAllAsReadByType(Long userId, String type) {
        String sql = "UPDATE notification SET is_read = TRUE, read_time = ? WHERE user_id = ? AND type = ? AND is_read = FALSE";

        executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                userId,
                type);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除通知失败，没有行被影响");
        }
    }

    @Override
    public void deleteByUserId(Long userId) {
        String sql = "DELETE FROM notification WHERE user_id = ?";
        executeUpdate(sql, userId);
    }

    @Override
    public void deleteOldNotifications(Long userId, int daysOld) {
        String sql = "DELETE FROM notification WHERE user_id = ? AND create_time < DATE_SUB(NOW(), INTERVAL ? DAY)";
        executeUpdate(sql, userId, daysOld);
    }
}