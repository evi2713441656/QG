package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.KnowledgeMemberDao;
import com.chenailin.www.model.pojo.KnowledgeMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class KnowledgeMemberDaoImpl extends BaseDao<KnowledgeMember> implements KnowledgeMemberDao {
    private static final Logger logger = LoggerFactory.getLogger(KnowledgeMemberDaoImpl.class);

    @Override
    protected KnowledgeMember mapResultSet(ResultSet rs) throws SQLException {
        KnowledgeMember km = new KnowledgeMember();
        km.setId(rs.getLong("id"));
        km.setKnowledgeId(rs.getLong("knowledge_id"));
        km.setUserId(rs.getLong("user_id"));
        km.setRole(rs.getInt("role"));
        km.setJoinTime(rs.getTimestamp("join_time"));
        return km;
    }

    @Override
    public KnowledgeMember findById(Long id) {
        String sql = "SELECT * FROM knowledge_member WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public KnowledgeMember findByKnowledgeAndUser(Long knowledgeId, Long userId) {
        String sql = "SELECT * FROM knowledge_member WHERE knowledge_id = ? AND user_id = ?";
        return executeQuerySingle(sql, knowledgeId, userId);
    }

    @Override
    public List<KnowledgeMember> findByKnowledgeId(Long knowledgeId) {
        String sql = "SELECT * FROM knowledge_member WHERE knowledge_id = ?";
        return executeQuery(sql, knowledgeId);
    }

    @Override
    public List<KnowledgeMember> findByUserId(Long userId) {
        String sql = "SELECT * FROM knowledge_member WHERE user_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public boolean exists(Long knowledgeId, Long userId) {
        String sql = "SELECT COUNT(*) FROM knowledge_member WHERE knowledge_id = ? AND user_id = ?";
        return executeCountQuery(sql, knowledgeId, userId) > 0;
    }

    @Override
    public void save(KnowledgeMember member) {
        String sql = "INSERT INTO knowledge_member (knowledge_id, user_id, role, join_time) VALUES (?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, member.getKnowledgeId(), member.getUserId(), member.getRole(), new java.sql.Timestamp(member.getJoinTime().getTime()));
        member.setId(id);
    }

    @Override
    public void update(KnowledgeMember member) {
        String sql = "UPDATE knowledge_member SET role = ? WHERE id = ?";
        executeUpdate(sql, member.getRole(), member.getId());
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM knowledge_member WHERE id = ?";
        executeUpdate(sql, id);
    }

    @Override
    public void deleteByKnowledgeAndUser(Long knowledgeId, Long userId) {
        String sql = "DELETE FROM knowledge_member WHERE knowledge_id = ? AND user_id = ?";
        executeUpdate(sql, knowledgeId, userId);
    }

    @Override
    public int countMembers(Long knowledgeId) {
        String sql = "SELECT COUNT(*) FROM knowledge_member WHERE knowledge_id = ?";
        return executeCountQuery(sql, knowledgeId);
    }
}