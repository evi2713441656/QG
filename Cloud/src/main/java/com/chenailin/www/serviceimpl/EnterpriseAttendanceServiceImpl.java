package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.*;
import com.chenailin.www.daoimpl.*;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.EnterpriseAttendanceDTO;
import com.chenailin.www.model.enums.EnterpriseRole;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.vo.EnterpriseAttendanceVO;
import com.chenailin.www.service.EnterpriseAttendanceService;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EnterpriseAttendanceService
 * @author evi
 */
public class EnterpriseAttendanceServiceImpl implements EnterpriseAttendanceService {
    private final EnterpriseAttendanceDao attendanceDao = new EnterpriseAttendanceDaoImpl();
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();
    private final EnterpriseMemberDao memberDao = new EnterpriseMemberDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final EnterpriseDepartmentDao departmentDao = new EnterpriseDepartmentDaoImpl();

    // 状态常量
    private static final int STATUS_NORMAL = 1;      // 正常
    private static final int STATUS_LATE = 2;        // 迟到
    private static final int STATUS_EARLY_LEAVE = 3; // 早退
    private static final int STATUS_ABSENT = 4;      // 缺勤

    // 标准工作时间配置（实际应从配置或数据库读取）
    private static final int WORK_START_HOUR = 9;  // 上班时间9:00
    private static final int WORK_START_MINUTE = 0;
    private static final int WORK_END_HOUR = 18;   // 下班时间18:00
    private static final int WORK_END_MINUTE = 0;
    private static final int LATE_THRESHOLD_MINUTES = 30;  // 迟到阈值30分钟
    private static final int EARLY_LEAVE_THRESHOLD_MINUTES = 30;  // 早退阈值30分钟

    @Override
    public EnterpriseAttendanceVO clockIn(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 获取今天的日期（只保留年月日）
        Date today = truncateToDay(new Date());
        Date now = new Date();

        // 检查今天是否已打卡
        EnterpriseAttendance attendance = attendanceDao.findByEnterpriseUserAndDate(enterpriseId, userId, today);
        if (attendance != null && attendance.getClockInTime() != null) {
            throw new BusinessException("今天已经打过卡了");
        }

        // 判断是否迟到
        int status = STATUS_NORMAL;
        if (isLate(now)) {
            status = STATUS_LATE;
        }

        // 创建或更新打卡记录
        if (attendance == null) {
            attendance = new EnterpriseAttendance();
            attendance.setEnterpriseId(enterpriseId);
            attendance.setUserId(userId);
            attendance.setDate(today);
            attendance.setClockInTime(now);
            attendance.setStatus(status);
            attendanceDao.save(attendance);
        } else {
            attendance.setClockInTime(now);
            attendance.setStatus(status);
            attendanceDao.update(attendance);
        }

        return convertToVO(attendance);
    }

    @Override
    public EnterpriseAttendanceVO clockOut(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 获取今天的日期（只保留年月日）
        Date today = truncateToDay(new Date());
        Date now = new Date();

        // 检查今天是否已打上班卡
        EnterpriseAttendance attendance = attendanceDao.findByEnterpriseUserAndDate(enterpriseId, userId, today);
        if (attendance == null || attendance.getClockInTime() == null) {
            throw new BusinessException("今天还没有打上班卡");
        }

        // 检查是否已经打过下班卡
        if (attendance.getClockOutTime() != null) {
            throw new BusinessException("今天已经打过下班卡了");
        }

        // 判断是否早退
        int status = attendance.getStatus();
        if (status != STATUS_LATE && isEarlyLeave(now)) {
            status = STATUS_EARLY_LEAVE;
        }

        // 更新打卡记录
        attendance.setClockOutTime(now);
        attendance.setStatus(status);
        attendanceDao.update(attendance);

        return convertToVO(attendance);
    }

    @Override
    public EnterpriseAttendanceVO getUserAttendanceStatus(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 获取今天的日期（只保留年月日）
        Date today = truncateToDay(new Date());

        // 获取今天的打卡记录
        EnterpriseAttendance attendance = attendanceDao.findByEnterpriseUserAndDate(enterpriseId, userId, today);

        // 如果没有记录，创建一个空记录用于返回状态
        if (attendance == null) {
            attendance = new EnterpriseAttendance();
            attendance.setEnterpriseId(enterpriseId);
            attendance.setUserId(userId);
            attendance.setDate(today);
            attendance.setStatus(0); // 未打卡状态
        }

        return convertToVO(attendance);
    }

    @Override
    public List<EnterpriseAttendanceVO> listAttendanceRecords(Long enterpriseId, Long userId,
                                                              Date startDate, Date endDate,
                                                              Long departmentId, int page, int size) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员且有权限查看所有记录
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 只有所有者、管理员或部门主管可以查看所有记录
        boolean canViewAll = member.getRole() <= EnterpriseRole.ADMIN.code;
        boolean isDepartmentManager = member.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code &&
                departmentId != null && departmentId.equals(member.getDepartmentId());

        if (!canViewAll && !isDepartmentManager) {
            throw new BusinessException("您没有权限查看所有考勤记录");
        }

        // 部门主管只能查看本部门记录
        if (isDepartmentManager && departmentId == null) {
            departmentId = member.getDepartmentId();
        }

        // 获取考勤记录
        List<EnterpriseAttendance> records = attendanceDao.findByEnterprise(
                enterpriseId, startDate, endDate, departmentId, size, (page - 1) * size);

        // 转换为VO
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public int countAttendanceRecords(Long enterpriseId, Long userId,
                                      Date startDate, Date endDate,
                                      Long departmentId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员且有权限查看所有记录
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 只有所有者、管理员或部门主管可以查看所有记录
        boolean canViewAll = member.getRole() <= EnterpriseRole.ADMIN.code;
        boolean isDepartmentManager = member.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code &&
                departmentId != null && departmentId.equals(member.getDepartmentId());

        if (!canViewAll && !isDepartmentManager) {
            throw new BusinessException("您没有权限查看所有考勤记录");
        }

        // 部门主管只能查看本部门记录
        if (isDepartmentManager && departmentId == null) {
            departmentId = member.getDepartmentId();
        }

        // 统计考勤记录数量
        return attendanceDao.countByEnterprise(enterpriseId, startDate, endDate, departmentId);
    }

    @Override
    public List<EnterpriseAttendanceVO> listUserAttendanceRecords(Long userId,
                                                                  Date startDate, Date endDate,
                                                                  int page, int size) {
        // 获取用户自己的考勤记录
        List<EnterpriseAttendance> records = attendanceDao.findByUser(
                userId, startDate, endDate, size, (page - 1) * size);

        // 转换为VO
        return records.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public int countUserAttendanceRecords(Long userId, Date startDate, Date endDate) {
        // 统计用户自己的考勤记录数量
        return 0; // 待实现，需要在EnterpriseAttendanceDao中添加此方法
    }

    @Override
    public EnterpriseAttendanceVO recordAttendance(EnterpriseAttendanceDTO dto, Long operatorId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(dto.getEnterpriseId());
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证操作者是企业所有者或管理员
        EnterpriseMember operator = memberDao.findByEnterpriseAndUser(dto.getEnterpriseId(), operatorId);
        if (operator == null || operator.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限手动记录考勤");
        }

        // 验证被记录的用户是企业成员
        EnterpriseMember targetMember = memberDao.findByEnterpriseAndUser(dto.getEnterpriseId(), dto.getUserId());
        if (targetMember == null) {
            throw new BusinessException("被记录的用户不是企业成员");
        }

        // 获取指定日期（只保留年月日）
        Date recordDate = truncateToDay(dto.getDate() != null ? dto.getDate() : new Date());

        // 检查该日期是否已有打卡记录
        EnterpriseAttendance attendance = attendanceDao.findByEnterpriseUserAndDate(
                dto.getEnterpriseId(), dto.getUserId(), recordDate);

        if (attendance == null) {
            // 创建新记录
            attendance = new EnterpriseAttendance();
            attendance.setEnterpriseId(dto.getEnterpriseId());
            attendance.setUserId(dto.getUserId());
            attendance.setDate(recordDate);
            attendance.setClockInTime(dto.getClockInTime());
            attendance.setClockOutTime(dto.getClockOutTime());
            attendance.setStatus(dto.getStatus() != null ? dto.getStatus() : determineStatus(dto.getClockInTime(), dto.getClockOutTime()));
            attendance.setNotes(dto.getNotes() != null ? dto.getNotes() : "管理员手动记录");

            attendanceDao.save(attendance);
        } else {
            // 更新现有记录
            if (dto.getClockInTime() != null) {
                attendance.setClockInTime(dto.getClockInTime());
            }

            if (dto.getClockOutTime() != null) {
                attendance.setClockOutTime(dto.getClockOutTime());
            }

            attendance.setStatus(dto.getStatus() != null ? dto.getStatus() :
                    determineStatus(attendance.getClockInTime(), attendance.getClockOutTime()));

            if (dto.getNotes() != null) {
                attendance.setNotes(dto.getNotes());
            } else if (attendance.getNotes() == null) {
                attendance.setNotes("管理员手动记录");
            }

            attendanceDao.update(attendance);
        }

        return convertToVO(attendance);
    }

    @Override
    public EnterpriseAttendanceVO updateAttendance(EnterpriseAttendanceDTO dto, Long operatorId) {
        // 验证考勤记录存在
        EnterpriseAttendance attendance = attendanceDao.findById(dto.getId());
        if (attendance == null) {
            throw new BusinessException("考勤记录不存在");
        }

        // 验证操作者是企业所有者或管理员
        EnterpriseMember operator = memberDao.findByEnterpriseAndUser(attendance.getEnterpriseId(), operatorId);
        if (operator == null || operator.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限修改考勤记录");
        }

        // 更新记录
        if (dto.getClockInTime() != null) {
            attendance.setClockInTime(dto.getClockInTime());
        }

        if (dto.getClockOutTime() != null) {
            attendance.setClockOutTime(dto.getClockOutTime());
        }

        if (dto.getStatus() != null) {
            attendance.setStatus(dto.getStatus());
        } else {
            attendance.setStatus(determineStatus(attendance.getClockInTime(), attendance.getClockOutTime()));
        }

        if (dto.getNotes() != null) {
            attendance.setNotes(dto.getNotes());
        }

        attendanceDao.update(attendance);
        return convertToVO(attendance);
    }

    @Override
    public void deleteAttendance(Long id, Long operatorId) {
        // 验证考勤记录存在
        EnterpriseAttendance attendance = attendanceDao.findById(id);
        if (attendance == null) {
            throw new BusinessException("考勤记录不存在");
        }

        // 验证操作者是企业所有者或管理员
        EnterpriseMember operator = memberDao.findByEnterpriseAndUser(attendance.getEnterpriseId(), operatorId);
        if (operator == null || operator.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限删除考勤记录");
        }

        // 删除记录
        attendanceDao.delete(id);
    }

    // 辅助方法：将日期截断到天（只保留年月日）
    private Date truncateToDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // 辅助方法：判断是否迟到
    private boolean isLate(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        // 设置标准上班时间
        Calendar workStart = Calendar.getInstance();
        workStart.setTime(time);
        workStart.set(Calendar.HOUR_OF_DAY, WORK_START_HOUR);
        workStart.set(Calendar.MINUTE, WORK_START_MINUTE);
        workStart.set(Calendar.SECOND, 0);
        workStart.set(Calendar.MILLISECOND, 0);

        // 加上迟到阈值
        workStart.add(Calendar.MINUTE, LATE_THRESHOLD_MINUTES);

        // 如果当前时间超过上班时间加迟到阈值，则视为迟到
        return calendar.after(workStart);
    }

    // 辅助方法：判断是否早退
    private boolean isEarlyLeave(Date time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);

        // 设置标准下班时间
        Calendar workEnd = Calendar.getInstance();
        workEnd.setTime(time);
        workEnd.set(Calendar.HOUR_OF_DAY, WORK_END_HOUR);
        workEnd.set(Calendar.MINUTE, WORK_END_MINUTE);
        workEnd.set(Calendar.SECOND, 0);
        workEnd.set(Calendar.MILLISECOND, 0);

        // 减去早退阈值
        workEnd.add(Calendar.MINUTE, -EARLY_LEAVE_THRESHOLD_MINUTES);

        // 如果当前时间早于下班时间减早退阈值，则视为早退
        return calendar.before(workEnd);
    }

    // 辅助方法：根据打卡时间判断状态
    private int determineStatus(Date clockInTime, Date clockOutTime) {
        if (clockInTime == null) {
            return STATUS_ABSENT; // 未打上班卡视为缺勤
        }

        if (isLate(clockInTime)) {
            return STATUS_LATE; // 迟到
        }

        if (clockOutTime != null && isEarlyLeave(clockOutTime)) {
            return STATUS_EARLY_LEAVE; // 早退
        }

        return STATUS_NORMAL; // 正常
    }

    // 辅助方法：计算工作时长（小时）
    private double calculateWorkHours(Date clockInTime, Date clockOutTime) {
        if (clockInTime == null || clockOutTime == null) {
            return 0;
        }

        // 计算毫秒差
        long diffMillis = clockOutTime.getTime() - clockInTime.getTime();

        // 转换为小时
        return diffMillis / (1000.0 * 60 * 60);
    }

    // 辅助方法：将EnterpriseAttendance转换为VO
    private EnterpriseAttendanceVO convertToVO(EnterpriseAttendance attendance) {
        EnterpriseAttendanceVO vo = new EnterpriseAttendanceVO();
        vo.setId(attendance.getId());
        vo.setEnterpriseId(attendance.getEnterpriseId());
        vo.setUserId(attendance.getUserId());
        vo.setDate(attendance.getDate());
        vo.setClockInTime(attendance.getClockInTime());
        vo.setClockOutTime(attendance.getClockOutTime());
        vo.setStatus(attendance.getStatus());
        vo.setNotes(attendance.getNotes());

        // 计算工作时长
        vo.setWorkHours(calculateWorkHours(attendance.getClockInTime(), attendance.getClockOutTime()));

        // 获取状态文本
        switch (attendance.getStatus()) {
            case STATUS_NORMAL:
                vo.setStatusText("正常");
                break;
            case STATUS_LATE:
                vo.setStatusText("迟到");
                break;
            case STATUS_EARLY_LEAVE:
                vo.setStatusText("早退");
                break;
            case STATUS_ABSENT:
                vo.setStatusText("缺勤");
                break;
            default:
                vo.setStatusText("未打卡");
        }

        // 获取企业名称
        Enterprise enterprise = enterpriseDao.findById(attendance.getEnterpriseId());
        if (enterprise != null) {
            vo.setEnterpriseName(enterprise.getName());
        }

        // 获取用户信息
        User user = userDao.findById(attendance.getUserId());
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }

        // 获取部门信息
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(attendance.getEnterpriseId(), attendance.getUserId());
        if (member != null && member.getDepartmentId() != null) {
            vo.setDepartmentId(member.getDepartmentId());

            EnterpriseDepartment department = departmentDao.findById(member.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getName());
            }
        }

        return vo;
    }
}