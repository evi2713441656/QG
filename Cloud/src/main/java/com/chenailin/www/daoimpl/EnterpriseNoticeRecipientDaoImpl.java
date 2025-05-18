package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseNoticeRecipientDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.EnterpriseNoticeRecipient;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * Implementation of EnterpriseNoticeRecipientDao
 * @author evi
 */
public class EnterpriseNoticeRecipientDaoImpl extends BaseDao<EnterpriseNoticeRecipient> implements EnterpriseNoticeRecipientDao {

    @Override
    protected EnterpriseNoticeRecipient mapResultSet(ResultSet rs) throws SQLException {
        EnterpriseNoticeRecipient recipient = new EnterpriseNoticeRecipient();
        recipient.setId(rs.getLong("id"));
        recipient.setNoticeId(rs.getLong("notice_id"));
        recipient.setUserId(rs.getLong("user_id"));
        recipient.setIsRead(rs.getBoolean("is_read"));
        recipient.setReadTime(rs.getTimestamp("read_time"));
        return recipient;
    }

    @Override
    public EnterpriseNoticeRecipient findById(Long id) {
        String sql = "SELECT * FROM enterprise_notice_recipient WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<EnterpriseNoticeRecipient> findByNoticeId(Long noticeId) {
        String sql = "SELECT * FROM enterprise_notice_recipient WHERE notice_id = ?";
        return executeQuery(sql, noticeId);
    }

    @Override
    public List<EnterpriseNoticeRecipient> findByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT r.* FROM enterprise_notice_recipient r " +
                "JOIN enterprise_notice n ON r.notice_id = n.id " +
                "WHERE r.user_id = ? " +
                "ORDER BY n.create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public List<EnterpriseNoticeRecipient> findUnreadByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT r.* FROM enterprise_notice_recipient r " +
                "JOIN enterprise_notice n ON r.notice_id = n.id " +
                "WHERE r.user_id = ? AND r.is_read = FALSE " +
                "ORDER BY n.create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public int countUnreadByUserId(Long userId) {
        String sql = "SELECT COUNT(*) FROM enterprise_notice_recipient WHERE user_id = ? AND is_read = FALSE";
        return executeCountQuery(sql, userId);
    }

    @Override
    public void save(EnterpriseNoticeRecipient recipient) {
        String sql = "INSERT INTO enterprise_notice_recipient (notice_id, user_id, is_read, read_time) VALUES (?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                recipient.getNoticeId(),
                recipient.getUserId(),
                recipient.isIsRead(),
                recipient.getReadTime() != null ? new java.sql.Timestamp(recipient.getReadTime().getTime()) : null);

        if (id == null) {
            throw new DataAccessException("创建通知接收者记录失败，无法获取ID");
        }
        recipient.setId(id);
    }

//    @Override
//    public void batchSave(List<EnterpriseNoticeRecipient> recipients) {
//        if (recipients == null || recipients.isEmpty()) {
//            return;
//        }
//
//        Connection conn = null;
//        PreparedStatement ps = null;
//        try {
//            conn = getConnection();
//            conn.setAutoCommit(false);
//
//            String sql = "INSERT INTO enterprise_notice_recipient (notice_id, user_id, is_read, read_time) VALUES (?, ?, ?, ?)";
//            ps = conn.prepareStatement(sql);
//
//            for (EnterpriseNoticeRecipient recipient : recipients) {
//                ps.setLong(1, recipient.getNoticeId());
//                ps.setLong(2, recipient.getUserId());
//                ps.setBoolean(3, recipient.isIsRead());
//                if (recipient.getReadTime() != null) {
//                    ps.setTimestamp(4, new java.sql.Timestamp(recipient.getReadTime().getTime()));
//                } else {
//                    ps.setNull(4, java.sql.Types.TIMESTAMP);
//                }
//                ps.addBatch();
//            }
//
//            ps.executeBatch();
//            conn.commit();
//        } catch (SQLException e) {
//            try {
//                if (conn != null) {
//                    conn.rollback();
//                }
//            } catch (SQLException ex) {
//                throw new DataAccessException("批量保存通知接收者记录回滚失败", ex);
//            }
//            throw new DataAccessException("批量保存通知接收者记录失败", e);
//        } finally {
//            release(conn, null, ps);
//        }
//    }

    @Override
    public void update(EnterpriseNoticeRecipient recipient) {
        String sql = "UPDATE enterprise_notice_recipient SET is_read = ?, read_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                recipient.isIsRead(),
                recipient.getReadTime() != null ? new java.sql.Timestamp(recipient.getReadTime().getTime()) : null,
                recipient.getId());

        if (affectedRows == 0) {
            throw new DataAccessException("更新通知接收者记录失败，没有行被影响");
        }
    }

    @Override
    public void markAsRead(Long noticeId, Long userId) {
        String sql = "UPDATE enterprise_notice_recipient SET is_read = TRUE, read_time = ? WHERE notice_id = ? AND user_id = ?";

        executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                noticeId,
                userId);
    }

    @Override
    public void markAllAsRead(Long userId) {
        String sql = "UPDATE enterprise_notice_recipient SET is_read = TRUE, read_time = ? WHERE user_id = ? AND is_read = FALSE";

        executeUpdate(sql,
                new java.sql.Timestamp(new Date().getTime()),
                userId);
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise_notice_recipient WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除通知接收者记录失败，没有行被影响");
        }
    }

    @Override
    public void deleteByNoticeId(Long noticeId) {
        String sql = "DELETE FROM enterprise_notice_recipient WHERE notice_id = ?";
        executeUpdate(sql, noticeId);
    }
}