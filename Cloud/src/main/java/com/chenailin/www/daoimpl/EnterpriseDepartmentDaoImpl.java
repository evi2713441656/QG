package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseDepartmentDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.EnterpriseDepartment;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * Implementation of EnterpriseDepartmentDao
 * @author evi
 */
public class EnterpriseDepartmentDaoImpl extends BaseDao<EnterpriseDepartment> implements EnterpriseDepartmentDao {

    @Override
    protected EnterpriseDepartment mapResultSet(ResultSet rs) throws SQLException {
        EnterpriseDepartment department = new EnterpriseDepartment();
        department.setId(rs.getLong("id"));
        department.setEnterpriseId(rs.getLong("enterprise_id"));
        department.setName(rs.getString("name"));
        department.setDescription(rs.getString("description"));
        department.setManagerId(rs.getLong("manager_id"));
        if (rs.wasNull()) {
            department.setManagerId(null);
        }
        department.setCreateTime(rs.getTimestamp("create_time"));
        department.setUpdateTime(rs.getTimestamp("update_time"));
        return department;
    }

    @Override
    public EnterpriseDepartment findById(Long id) {
        String sql = "SELECT * FROM enterprise_department WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<EnterpriseDepartment> findByEnterpriseId(Long enterpriseId) {
        String sql = "SELECT * FROM enterprise_department WHERE enterprise_id = ? ORDER BY name";
        return executeQuery(sql, enterpriseId);
    }

    @Override
    public List<EnterpriseDepartment> findByManagerId(Long userId) {
        String sql = "SELECT * FROM enterprise_department WHERE manager_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public boolean nameExists(Long enterpriseId, String name, Long excludeId) {
        String sql = "SELECT COUNT(*) FROM enterprise_department WHERE enterprise_id = ? AND name = ? AND id != ?";
        int count = executeCountQuery(sql, enterpriseId, name, excludeId == null ? -1 : excludeId);
        return count > 0;
    }

    @Override
    public void save(EnterpriseDepartment department) {
        String sql = "INSERT INTO enterprise_department (enterprise_id, name, description, manager_id, create_time, update_time) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                department.getEnterpriseId(),
                department.getName(),
                department.getDescription(),
                department.getManagerId(),
                new java.sql.Timestamp(department.getCreateTime().getTime()),
                new java.sql.Timestamp(department.getUpdateTime().getTime()));

        if (id == null) {
            throw new DataAccessException("创建部门失败，无法获取ID");
        }
        department.setId(id);
    }

    @Override
    public void update(EnterpriseDepartment department) {
        String sql = "UPDATE enterprise_department SET name = ?, description = ?, manager_id = ?, update_time = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                department.getName(),
                department.getDescription(),
                department.getManagerId(),
                new java.sql.Timestamp(department.getUpdateTime().getTime()),
                department.getId());

        if (affectedRows == 0) {
            throw new DataAccessException("更新部门失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise_department WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除部门失败，没有行被影响");
        }
    }

    @Override
    public void deleteByEnterpriseId(Long enterpriseId) {
        String sql = "DELETE FROM enterprise_department WHERE enterprise_id = ?";
        executeUpdate(sql, enterpriseId);
    }
}