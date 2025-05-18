package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.EnterpriseAttendance;
import java.util.Date;
import java.util.List;

/**
 * Data Access Object interface for enterprise attendance operations
 * @author evi
 */
public interface EnterpriseAttendanceDao {
    EnterpriseAttendance findById(Long id);
    EnterpriseAttendance findByEnterpriseUserAndDate(Long enterpriseId, Long userId, Date date);
    List<EnterpriseAttendance> findByEnterprise(
            Long enterpriseId,
            Date startDate,
            Date endDate,
            Long departmentId,
            int limit,
            int offset);
    int countByEnterprise(Long enterpriseId, Date startDate, Date endDate, Long departmentId);
    List<EnterpriseAttendance> findByUser(Long userId, Date startDate, Date endDate, int limit, int offset);
    void save(EnterpriseAttendance attendance);
    void update(EnterpriseAttendance attendance);
    void delete(Long id);
}