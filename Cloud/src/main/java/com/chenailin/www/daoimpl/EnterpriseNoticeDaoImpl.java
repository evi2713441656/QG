package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseNoticeDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.EnterpriseNotice;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of EnterpriseNoticeDao
 * @author evi
 */
public class EnterpriseNoticeDaoImpl extends BaseDao<EnterpriseNotice> implements EnterpriseNoticeDao {

    @Override
    protected EnterpriseNotice mapResultSet(ResultSet rs) throws SQLException {
        EnterpriseNotice notice = new EnterpriseNotice();
        notice.setId(rs.getLong("id"));
        notice.setEnterpriseId(rs.getLong("enterprise_id"));
        notice.setTitle(rs.getString("title"));
        notice.setContent(rs.getString("content"));
        notice.setPublisherId(rs.getLong("publisher_id"));
        notice.setIsPrivate(rs.getBoolean("is_private"));
        notice.setCreateTime(rs.getTimestamp("create_time"));
        notice.setUpdateTime(rs.getTimestamp("update_time"));
        return notice;
    }

    @Override
    public EnterpriseNotice findById(Long id) {
        String sql = "SELECT * FROM enterprise_notice WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<EnterpriseNotice> findByEnterpriseId(Long enterpriseId, int limit, int offset) {
        String sql = "SELECT * FROM enterprise_notice WHERE enterprise_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, enterpriseId, limit, offset);
    }

    @Override
    public int countByEnterpriseId(Long enterpriseId) {
        String sql = "SELECT COUNT(*) FROM enterprise_notice WHERE enterprise_id = ?";
        return executeCountQuery(sql, enterpriseId);
    }

    @Override
    public List<EnterpriseNotice> findByPublisherId(Long userId, int limit, int offset) {
        String sql = "SELECT * FROM enterprise_notice WHERE publisher_id = ? ORDER BY create_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public void save(EnterpriseNotice notice) {
        String sql = "INSERT INTO enterprise_notice (enterprise_id, title, content, publisher_id, is_private, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                notice.getEnterpriseId(),
                notice.getTitle(),
                notice.getContent(),
                notice.getPublisherId(),
                notice.isIsPrivate(),
                new java.sql.Timestamp(notice.getCreateTime().getTime()),
                new java.sql.Timestamp(notice.getUpdateTime().getTime()));

        if (id == null) {
            throw new DataAccessException("创建企业通知失败，无法获取ID");
        }
        notice.setId(id);
    }

    @Override
    public void update(EnterpriseNotice notice) {
        String sql = "UPDATE enterprise_notice SET title = ?, content = ?, is_private = ?, update_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                notice.getTitle(),
                notice.getContent(),
                notice.isIsPrivate(),
                new java.sql.Timestamp(notice.getUpdateTime().getTime()),
                notice.getId());

        if (affectedRows == 0) {
            throw new DataAccessException("更新企业通知失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise_notice WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除企业通知失败，没有行被影响");
        }
    }

    @Override
    public void deleteByEnterpriseId(Long enterpriseId) {
        String sql = "DELETE FROM enterprise_notice WHERE enterprise_id = ?";
        executeUpdate(sql, enterpriseId);
    }
}