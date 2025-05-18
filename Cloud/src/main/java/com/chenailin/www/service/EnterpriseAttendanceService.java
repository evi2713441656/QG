package com.chenailin.www.service;

import com.chenailin.www.model.dto.EnterpriseAttendanceDTO;
import com.chenailin.www.model.vo.EnterpriseAttendanceVO;

import java.util.Date;
import java.util.List;

/**
 * Enterprise Attendance Service Interface
 * @author evi
 */
public interface EnterpriseAttendanceService {
    EnterpriseAttendanceVO clockIn(Long enterpriseId, Long userId);
    EnterpriseAttendanceVO clockOut(Long enterpriseId, Long userId);
    EnterpriseAttendanceVO getUserAttendanceStatus(Long enterpriseId, Long userId);
    List<EnterpriseAttendanceVO> listAttendanceRecords(Long enterpriseId, Long userId, Date startDate, Date endDate, Long departmentId, int page, int size);
    int countAttendanceRecords(Long enterpriseId, Long userId, Date startDate, Date endDate, Long departmentId);
    List<EnterpriseAttendanceVO> listUserAttendanceRecords(Long userId, Date startDate, Date endDate, int page, int size);
    int countUserAttendanceRecords(Long userId, Date startDate, Date endDate);
    EnterpriseAttendanceVO recordAttendance(EnterpriseAttendanceDTO dto, Long operatorId);
    EnterpriseAttendanceVO updateAttendance(EnterpriseAttendanceDTO dto, Long operatorId);
    void deleteAttendance(Long id, Long operatorId);
}