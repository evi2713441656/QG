package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.BrowseHistoryDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.BrowseHistory;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class BrowseHistoryDaoImpl extends BaseDao<BrowseHistory> implements BrowseHistoryDao {
    // 每个用户最多保留的浏览记录数
    private static final int MAX_HISTORY_RECORDS = 100;

    @Override
    protected BrowseHistory mapResultSet(ResultSet rs) throws SQLException {
        BrowseHistory history = new BrowseHistory();
        history.setId(rs.getLong("id"));
        history.setUserId(rs.getLong("user_id"));
        history.setArticleId(rs.getLong("article_id"));
        history.setBrowseTime(rs.getTimestamp("browse_time"));
        return history;
    }

    @Override
    public List<BrowseHistory> findByUserId(Long userId, int limit, int offset) {
        String sql = "SELECT * FROM browse_history WHERE user_id = ? ORDER BY browse_time DESC LIMIT ? OFFSET ?";
        return executeQuery(sql, userId, limit, offset);
    }

    @Override
    public void save(BrowseHistory browseHistory) {
        // 先检查是否已存在相同的记录，如果存在则更新浏览时间
        String checkSql = "SELECT id FROM browse_history WHERE user_id = ? AND article_id = ?";
        BrowseHistory existing = executeQuerySingle(checkSql, browseHistory.getUserId(), browseHistory.getArticleId());
        if (existing != null) {
            // 存在记录，更新浏览时间
            String updateSql = "UPDATE browse_history SET browse_time = ? WHERE id = ?";
            executeUpdate(updateSql, new java.sql.Timestamp(browseHistory.getBrowseTime().getTime()), existing.getId());
        } else {
            // 不存在记录，插入新记录
            String insertSql = "INSERT INTO browse_history (user_id, article_id, browse_time) VALUES (?, ?, ?)";
            Long id = executeInsertWithGeneratedKey(insertSql, browseHistory.getUserId(), browseHistory.getArticleId(),
                    new java.sql.Timestamp(browseHistory.getBrowseTime().getTime()));
            if (id != null) {
                browseHistory.setId(id);
            }

            // 检查记录数量是否超过上限，如果超过则删除最早的记录
            try {
                cleanupOldRecords(browseHistory.getUserId());
            } catch (SQLException e) {
                throw new DataAccessException("清理旧的浏览记录失败", e);
            }
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM browse_history WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public int deleteByUserId(Long userId) {
        String sql = "DELETE FROM browse_history WHERE user_id = ?";
        return executeUpdate(sql, userId);
    }

    private void cleanupOldRecords(Long userId) throws SQLException {
        // 获取当前记录数
        String countSql = "SELECT COUNT(*) FROM browse_history WHERE user_id = ?";
        int count = executeCountQuery(countSql, userId);
        if (count > MAX_HISTORY_RECORDS) {
            // 删除最早的记录
            String deleteSql = "DELETE FROM browse_history WHERE id IN (SELECT id FROM browse_history WHERE user_id = ? ORDER BY browse_time ASC LIMIT ?)";
            executeUpdate(deleteSql, userId, count - MAX_HISTORY_RECORDS);
        }
    }
}