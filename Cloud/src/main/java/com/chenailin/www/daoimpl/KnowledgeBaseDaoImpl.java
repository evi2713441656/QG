package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.KnowledgeBaseDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.KnowledgeBase;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class KnowledgeBaseDaoImpl extends BaseDao<KnowledgeBase> implements KnowledgeBaseDao {

    @Override
    protected KnowledgeBase mapResultSet(ResultSet rs) throws SQLException {
        KnowledgeBase kb = new KnowledgeBase();
        kb.setId(rs.getLong("id"));
        kb.setName(rs.getString("name"));
        kb.setDescription(rs.getString("description"));
        kb.setCoverUrl(rs.getString("cover_url"));
        kb.setIsPublic(rs.getBoolean("is_public"));
        kb.setCreatorId(rs.getLong("creator_id"));
        kb.setCreateTime(rs.getTimestamp("create_time"));
        kb.setUpdateTime(rs.getTimestamp("update_time"));
        return kb;
    }

    @Override
    public KnowledgeBase findById(Long id) {
        String sql = "SELECT * FROM knowledge_base WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<KnowledgeBase> findByUserId(Long userId) {
        String sql = "SELECT * FROM knowledge_base WHERE creator_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public List<KnowledgeBase> findPublicBases() {
        String sql = "SELECT * FROM knowledge_base WHERE is_public = true";
        return executeQuery(sql);
    }

    @Override
    public void save(KnowledgeBase knowledgeBase) {
        String sql = "INSERT INTO knowledge_base (name, description, cover_url, is_public, creator_id, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, knowledgeBase.getName(), knowledgeBase.getDescription(), knowledgeBase.getCoverUrl(),
                knowledgeBase.getIsPublic(), knowledgeBase.getCreatorId(), new java.sql.Timestamp(System.currentTimeMillis()),
                new java.sql.Timestamp(System.currentTimeMillis()));
        if (id == null) {
            throw new DataAccessException("创建知识库失败，无法获取ID");
        }
        knowledgeBase.setId(id);
    }

    @Override
    public void update(KnowledgeBase knowledgeBase) {
        String sql = "UPDATE knowledge_base SET name = ?, description = ?, is_public = ?, update_time = ? " +
                "WHERE id = ?";
        int affectedRows = executeUpdate(sql, knowledgeBase.getName(), knowledgeBase.getDescription(), knowledgeBase.getIsPublic(),
                new java.sql.Timestamp(System.currentTimeMillis()), knowledgeBase.getId());
        if (affectedRows == 0) {
            throw new DataAccessException("更新知识库失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM knowledge_base WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除知识库失败，没有行被影响");
        }
    }

    @Override
    public boolean checkNameExists(String name, Long excludeId) {
        String sql = "SELECT COUNT(*) FROM knowledge_base WHERE name = ? AND id != ?";
        int count = executeCountQuery(sql, name, excludeId != null ? excludeId : -1);
        return count > 0;
    }

//    @Override
//    public List<KnowledgeBase> searchByKeyword(String keyword, int limit, int offset) {
//        String sql = "SELECT * FROM knowledge_base WHERE MATCH (title, content) AGAINST (? IN NATURAL LANGUAGE MODE) LIMIT ? OFFSET ?";
//        return executeQuery(sql, keyword, limit, offset);
//    }
}