package com.chenailin.www.daoimpl;

import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.util.DBUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author evi
 */
public abstract class BaseDao<T> {
    private static final Logger logger = LoggerFactory.getLogger(BaseDao.class);

    // 抽象方法，用于将 ResultSet 映射为实体对象
    protected abstract T mapResultSet(ResultSet rs) throws SQLException;

    // 执行查询操作，返回实体对象列表
    protected List<T> executeQuery(String sql, Object... params) {
        List<T> result = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.add(mapResultSet(rs));
            }
        } catch (SQLException e) {
            logger.error("执行查询操作失败, SQL: " + sql, e);
            throw new DataAccessException("执行查询操作失败", e);
        }
        return result;
    }

    // 执行查询操作，返回单个实体对象
    protected T executeQuerySingle(String sql, Object... params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSet(rs);
            }
        } catch (SQLException e) {
            logger.error("执行查询单个对象操作失败, SQL: " + sql, e);
            throw new DataAccessException("执行查询单个对象操作失败", e);
        }
        return null;
    }

    // 执行插入、更新、删除操作
    protected int executeUpdate(String sql, Object... params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            return stmt.executeUpdate();
        } catch (SQLException e) {
            logger.error("执行更新操作失败, SQL: " + sql, e);
            throw new DataAccessException("执行更新操作失败", e);
        }
    }

    // 执行插入操作并返回生成的主键
    protected Long executeInsertWithGeneratedKey(String sql, Object... params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setParams(stmt, params);
            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                throw new DataAccessException("插入操作失败，没有行被影响");
            }
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getLong(1);
                } else {
                    throw new DataAccessException("插入操作失败，无法获取生成的主键");
                }
            }
        } catch (SQLException e) {
            logger.error("执行插入操作并获取主键失败, SQL: " + sql, e);
            throw new DataAccessException("执行插入操作并获取主键失败", e);
        }
    }

    // 设置 PreparedStatement 的参数
    private void setParams(PreparedStatement stmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
    }

    // 执行统计查询操作，返回统计结果
    protected int executeCountQuery(String sql, Object... params) {
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParams(stmt, params);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            logger.error("执行统计查询操作失败, SQL: " + sql, e);
            throw new DataAccessException("执行统计查询操作失败", e);
        }
    }
}