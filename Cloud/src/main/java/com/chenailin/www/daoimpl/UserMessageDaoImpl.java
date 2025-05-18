package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.UserMessageDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.UserMessage;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Implementation of UserMessageDao
 * @author evi
 */
public class UserMessageDaoImpl extends BaseDao<UserMessage> implements UserMessageDao {

    @Override
    protected UserMessage mapResultSet(ResultSet rs) throws SQLException {
        UserMessage message = new UserMessage();
        message.setId(rs.getLong("id"));
        message.setSenderId(rs.getLong("sender_id"));
        message.setRecipientId(rs.getLong("recipient_id"));
        message.setContent(rs.getString("content"));
        message.setIsRead(rs.getBoolean("is_read"));
        message.setReadTime(rs.getTimestamp("read_time"));
        message.setCreateTime(rs.getTimestamp("create_time"));
        return message;
    }

    @Override
    public UserMessage findById(Long id) {
        String sql = "SELECT * FROM user_message WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<UserMessage> findBySenderId(Long senderId, int limit, int offset) {
        String sql = "SELECT * FROM user_message WHERE sender_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, senderId, limit, offset);
    }

    @Override
    public List<UserMessage> findByRecipientId(Long recipientId, int limit, int offset) {
        String sql = "SELECT * FROM user_message WHERE recipient_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, recipientId, limit, offset);
    }

    @Override
    public List<UserMessage> findUnreadByRecipientId(Long recipientId, int limit, int offset) {
        String sql = "SELECT * FROM user_message WHERE recipient_id = ? AND is_read = FALSE ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, recipientId, limit, offset);
    }

    @Override
    public int countUnreadByRecipientId(Long recipientId) {
        String sql = "SELECT COUNT(*) FROM user_message WHERE recipient_id = ? AND is_read = FALSE";
        return executeCountQuery(sql, recipientId);
    }

    @Override
    public List<UserMessage> findConversation(Long userId1, Long userId2, int limit, int offset) {
        String sql = "SELECT * FROM user_message " +
                "WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?) " +
                "ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId1, userId2, userId2, userId1, limit, offset);
    }

    @Override
    public List<UserMessage> findNewConversationMessages(Long userId1, Long userId2, Long lastMessageId) {
        String sql = "SELECT * FROM user_message " +
                "WHERE ((sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?)) " +
                "AND id > ? " +
                "ORDER BY create_time ASC";
        return executeQuery(sql, userId1, userId2, userId2, userId1, lastMessageId);
    }

    @Override
    public void save(UserMessage message) {
        String sql = "INSERT INTO user_message (sender_id, recipient_id, content, is_read, read_time, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                message.getSenderId(),
                message.getRecipientId(),
                message.getContent(),
                message.isIsRead(),
                message.getReadTime() != null ? new java.sql.Timestamp(message.getReadTime().getTime()) : null,
                new java.sql.Timestamp(message.getCreateTime().getTime()));

        if (id == null) {
            throw new DataAccessException("创建消息失败，无法获取ID");
        }
        message.setId(id);
    }

    @Override
    public void markAsRead(Long messageId) {
        String sql = "UPDATE user_message SET is_read = TRUE, read_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                messageId);

        if (affectedRows == 0) {
            throw new DataAccessException("标记消息已读失败，没有行被影响");
        }
    }

    @Override
    public void markAllAsRead(Long senderId, Long recipientId) {
        String sql = "UPDATE user_message SET is_read = TRUE, read_time = ? " +
                "WHERE sender_id = ? AND recipient_id = ? AND is_read = FALSE";

        executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                senderId,
                recipientId);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM user_message WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除消息失败，没有行被影响");
        }
    }

    @Override
    public void deleteConversation(Long userId1, Long userId2) {
        String sql = "DELETE FROM user_message " +
                "WHERE (sender_id = ? AND recipient_id = ?) OR (sender_id = ? AND recipient_id = ?)";

        executeUpdate(sql, userId1, userId2, userId2, userId1);
    }
}