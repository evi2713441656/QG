package com.chenailin.www.daoimpl;

import com.chenailin.www.dao.EnterpriseAttendanceDao;
import com.chenailin.www.exception.DataAccessException;
import com.chenailin.www.model.pojo.EnterpriseAttendance;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Implementation of EnterpriseAttendanceDao
 * @author evi
 */
public class EnterpriseAttendanceDaoImpl extends BaseDao<EnterpriseAttendance> implements EnterpriseAttendanceDao {

    @Override
    protected EnterpriseAttendance mapResultSet(ResultSet rs) throws SQLException {
        EnterpriseAttendance attendance = new EnterpriseAttendance();
        attendance.setId(rs.getLong("id"));
        attendance.setEnterpriseId(rs.getLong("enterprise_id"));
        attendance.setUserId(rs.getLong("user_id"));
        attendance.setDate(rs.getDate("date"));
        attendance.setClockInTime(rs.getTimestamp("clock_in_time"));
        attendance.setClockOutTime(rs.getTimestamp("clock_out_time"));
        attendance.setStatus(rs.getInt("status"));
        attendance.setNotes(rs.getString("notes"));
        return attendance;
    }

    @Override
    public EnterpriseAttendance findById(Long id) {
        String sql = "SELECT * FROM enterprise_attendance WHERE id = ?";
        return executeQuerySingle(sql, id);
    }

    @Override
    public EnterpriseAttendance findByEnterpriseUserAndDate(Long enterpriseId, Long userId, Date date) {
        String sql = "SELECT * FROM enterprise_attendance WHERE enterprise_id = ? AND user_id = ? AND date = ?";
        return executeQuerySingle(sql, enterpriseId, userId, new java.sql.Date(date.getTime()));
    }

    @Override
    public List<EnterpriseAttendance> findByEnterprise(Long enterpriseId, Date startDate, Date endDate, Long departmentId, int limit, int offset) {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT a.* FROM enterprise_attendance a " +
                        "JOIN enterprise_member m ON a.user_id = m.user_id AND a.enterprise_id = m.enterprise_id " +
                        "WHERE a.enterprise_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(enterpriseId);

        if (startDate != null) {
            sqlBuilder.append("AND a.date >= ? ");
            params.add(new java.sql.Date(startDate.getTime()));
        }

        if (endDate != null) {
            sqlBuilder.append("AND a.date <= ? ");
            params.add(new java.sql.Date(endDate.getTime()));
        }

        if (departmentId != null) {
            sqlBuilder.append("AND m.department_id = ? ");
            params.add(departmentId);
        }

        sqlBuilder.append("ORDER BY a.date DESC, a.clock_in_time DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return executeQuery(sqlBuilder.toString(), params.toArray());
    }

    @Override
    public int countByEnterprise(Long enterpriseId, Date startDate, Date endDate, Long departmentId) {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT COUNT(*) FROM enterprise_attendance a " +
                        "JOIN enterprise_member m ON a.user_id = m.user_id AND a.enterprise_id = m.enterprise_id " +
                        "WHERE a.enterprise_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(enterpriseId);

        if (startDate != null) {
            sqlBuilder.append("AND a.date >= ? ");
            params.add(new java.sql.Date(startDate.getTime()));
        }

        if (endDate != null) {
            sqlBuilder.append("AND a.date <= ? ");
            params.add(new java.sql.Date(endDate.getTime()));
        }

        if (departmentId != null) {
            sqlBuilder.append("AND m.department_id = ? ");
            params.add(departmentId);
        }

        return executeCountQuery(sqlBuilder.toString(), params.toArray());
    }

    @Override
    public List<EnterpriseAttendance> findByUser(Long userId, Date startDate, Date endDate, int limit, int offset) {
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM enterprise_attendance WHERE user_id = ? ");

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (startDate != null) {
            sqlBuilder.append("AND date >= ? ");
            params.add(new java.sql.Date(startDate.getTime()));
        }

        if (endDate != null) {
            sqlBuilder.append("AND date <= ? ");
            params.add(new java.sql.Date(endDate.getTime()));
        }

        sqlBuilder.append("ORDER BY date DESC, clock_in_time DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        return executeQuery(sqlBuilder.toString(), params.toArray());
    }

    @Override
    public void save(EnterpriseAttendance attendance) {
        String sql = "INSERT INTO enterprise_attendance (enterprise_id, user_id, date, clock_in_time, clock_out_time, status, notes) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        Long id = executeInsertWithGeneratedKey(sql,
                attendance.getEnterpriseId(),
                attendance.getUserId(),
                new java.sql.Date(attendance.getDate().getTime()),
                attendance.getClockInTime() != null ? new java.sql.Timestamp(attendance.getClockInTime().getTime()) : null,
                attendance.getClockOutTime() != null ? new java.sql.Timestamp(attendance.getClockOutTime().getTime()) : null,
                attendance.getStatus(),
                attendance.getNotes());

        if (id == null) {
            throw new DataAccessException("创建考勤记录失败，无法获取ID");
        }
        attendance.setId(id);
    }

    @Override
    public void update(EnterpriseAttendance attendance) {
        String sql = "UPDATE enterprise_attendance SET clock_in_time = ?, clock_out_time = ?, status = ?, notes = ? WHERE id = ?";

        int affectedRows = executeUpdate(sql,
                attendance.getClockInTime() != null ? new java.sql.Timestamp(attendance.getClockInTime().getTime()) : null,
                attendance.getClockOutTime() != null ? new java.sql.Timestamp(attendance.getClockOutTime().getTime()) : null,
                attendance.getStatus(),
                attendance.getNotes(),
                attendance.getId());

        if (affectedRows == 0) {
            throw new DataAccessException("更新考勤记录失败，没有行被影响");
        }
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM enterprise_attendance WHERE id = ?";
        int affectedRows = executeUpdate(sql, id);
        if (affectedRows == 0) {
            throw new DataAccessException("删除考勤记录失败，没有行被影响");
        }
    }
}