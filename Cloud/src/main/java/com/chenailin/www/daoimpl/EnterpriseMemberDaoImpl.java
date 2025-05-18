package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseMemberDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.EnterpriseMember;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of EnterpriseMemberDao
 * @author evi
 */
public class EnterpriseMemberDaoImpl extends BaseDao<EnterpriseMember> implements EnterpriseMemberDao {

    @Override
    protected EnterpriseMember mapResultSet(ResultSet rs) throws SQLException {
        EnterpriseMember member = new EnterpriseMember();
        member.setId(rs.getLong("id"));
        member.setEnterpriseId(rs.getLong("enterprise_id"));
        member.setUserId(rs.getLong("user_id"));
        member.setRole(rs.getInt("role"));
        member.setDepartmentId(rs.getLong("department_id"));
        if (rs.wasNull()) {
            member.setDepartmentId(null);
        }
        member.setJoinTime(rs.getTimestamp("join_time"));
        return member;
    }

    @Override
    public EnterpriseMember findById(Long id) {
        String sql = "SELECT * FROM enterprise_member WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public EnterpriseMember findByEnterpriseAndUser(Long enterpriseId, Long userId) {
        String sql = "SELECT * FROM enterprise_member WHERE enterprise_id = ? AND user_id = ?";
        return executeQuerySingle(sql, enterpriseId, userId);
    }

    @Override
    public List<EnterpriseMember> findByEnterpriseId(Long enterpriseId) {
        String sql = "SELECT * FROM enterprise_member WHERE enterprise_id = ? ORDER BY role ASC, join_time ASC";
        return executeQuery(sql, enterpriseId);
    }

    @Override
    public int countMembers(Long enterpriseId) {
        String sql = "SELECT COUNT(*) FROM enterprise_member WHERE enterprise_id = ?";
        return executeCountQuery(sql, enterpriseId);
    }

    @Override
    public boolean exists(Long enterpriseId, Long userId) {
        String sql = "SELECT COUNT(*) FROM enterprise_member WHERE enterprise_id = ? AND user_id = ?";
        int count = executeCountQuery(sql, enterpriseId, userId);
        return count > 0;
    }

    @Override
    public void save(EnterpriseMember member) {
        String sql = "INSERT INTO enterprise_member (enterprise_id, user_id, role, department_id, join_time) VALUES (?, ?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql,
                member.getEnterpriseId(),
                member.getUserId(),
                member.getRole(),
                member.getDepartmentId(),
                new java.sql.Timestamp(member.getJoinTime().getTime()));

        if (id == null) {
            throw new DataAccessException("添加企业成员失败，无法获取ID");
        }
        member.setId(id);
    }

    @Override
    public void update(EnterpriseMember member) {
        String sql = "UPDATE enterprise_member SET role = ?, department_id = ? WHERE id = ?";
        int affectedRows = executeUpdate(sql, member.getRole(), member.getDepartmentId(), member.getId());
        if (affectedRows == 0) {
            throw new DataAccessException("更新企业成员失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise_member WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除企业成员失败，没有行被影响");
        }
    }

    @Override
    public void deleteByEnterpriseId(Long enterpriseId) {
        String sql = "DELETE FROM enterprise_member WHERE enterprise_id = ?";
        executeUpdate(sql, enterpriseId);
    }

    @Override
    public List<EnterpriseMember> findByUserIdAndRole(Long userId, int role) {
        String sql = "SELECT * FROM enterprise_member WHERE user_id = ? AND role = ?";
        return executeQuery(sql, userId, role);
    }

    @Override
    public List<EnterpriseMember> findByDepartmentId(Long departmentId) {
        String sql = "SELECT * FROM enterprise_member WHERE department_id = ? ORDER BY role ASC, join_time ASC";
        return executeQuery(sql, departmentId);
    }
}