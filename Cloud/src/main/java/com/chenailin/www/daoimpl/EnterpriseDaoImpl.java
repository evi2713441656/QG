package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.Enterprise;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author evi
 */
public class EnterpriseDaoImpl extends BaseDao<Enterprise> implements EnterpriseDao {

    @Override
    protected Enterprise mapResultSet(ResultSet rs) throws SQLException {
        Enterprise enterprise = new Enterprise();
        enterprise.setId(rs.getLong("id"));
        enterprise.setName(rs.getString("name"));
        enterprise.setCreatorId(rs.getLong("creator_id"));
        enterprise.setCreateTime(rs.getTimestamp("create_time"));
        enterprise.setUpdateTime(rs.getTimestamp("update_time"));
        return enterprise;
    }

    @Override
    public Enterprise findById(Long id) {
        String sql = "SELECT * FROM enterprise WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public List<Enterprise> findByCreatorId(Long creatorId) {
        String sql = "SELECT * FROM enterprise WHERE creator_id = ?";
        return executeQuery(sql, creatorId);
    }

    @Override
    public List<Enterprise> findByUserId(Long userId) {
        String sql = "SELECT e.* FROM enterprise e JOIN enterprise_member m ON e.id = m.enterprise_id WHERE m.user_id = ?";
        return executeQuery(sql, userId);
    }

    @Override
    public void save(Enterprise enterprise) {
        String sql = "INSERT INTO enterprise (name, creator_id, create_time, update_time) VALUES (?, ?, ?, ?)";
        Long id = executeInsertWithGeneratedKey(sql, enterprise.getName(), enterprise.getCreatorId(),
                new java.sql.Timestamp(enterprise.getCreateTime().getTime()), new java.sql.Timestamp(enterprise.getUpdateTime().getTime()));
        if (id == null) {
            throw new DataAccessException("创建企业失败，无法获取ID");
        }
        enterprise.setId(id);
    }

    @Override
    public void update(Enterprise enterprise) {
        String sql = "UPDATE enterprise SET name = ?, update_time = ? WHERE id = ?";
        int affectedRows = executeUpdate(sql, enterprise.getName(), new java.sql.Timestamp(enterprise.getUpdateTime().getTime()), enterprise.getId());
        if (affectedRows == 0) {
            throw new DataAccessException("更新企业失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除企业失败，没有行被影响");
        }
    }

    @Override
    public boolean exists(String name) {
        String sql = "SELECT COUNT(*) FROM enterprise WHERE name = ?";
        int count = executeCountQuery(sql, name);
        return count > 0;
    }
}