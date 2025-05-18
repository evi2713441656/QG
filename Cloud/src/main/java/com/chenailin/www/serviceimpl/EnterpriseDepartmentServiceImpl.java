package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.EnterpriseDepartmentDao;
import com.chenailin.www.dao.EnterpriseMemberDao;
import com.chenailin.www.dao.EnterpriseDao;
import com.chenailin.www.dao.UserDao;
import com.chenailin.www.daoimpl.EnterpriseDepartmentDaoImpl;
import com.chenailin.www.daoimpl.EnterpriseMemberDaoImpl;
import com.chenailin.www.daoimpl.EnterpriseDaoImpl;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.EnterpriseDepartmentDTO;
import com.chenailin.www.model.enums.EnterpriseRole;
import com.chenailin.www.model.pojo.Enterprise;
import com.chenailin.www.model.pojo.EnterpriseDepartment;
import com.chenailin.www.model.pojo.EnterpriseMember;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.vo.EnterpriseDepartmentVO;
import com.chenailin.www.model.vo.EnterpriseMemberVO;
import com.chenailin.www.service.EnterpriseDepartmentService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EnterpriseDepartmentService
 * @author evi
 */
public class EnterpriseDepartmentServiceImpl implements EnterpriseDepartmentService {
    private final EnterpriseDepartmentDao departmentDao = new EnterpriseDepartmentDaoImpl();
    private final EnterpriseMemberDao memberDao = new EnterpriseMemberDaoImpl();
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public EnterpriseDepartment createDepartment(EnterpriseDepartmentDTO dto, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(dto.getEnterpriseId());
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：只有所有者和管理员可以创建部门
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(dto.getEnterpriseId(), userId);
        if (currentMember == null || currentMember.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限创建部门");
        }

        // 检查部门名称是否已存在
        if (departmentDao.nameExists(dto.getEnterpriseId(), dto.getName(), null)) {
            throw new BusinessException("部门名称已存在");
        }

        // 创建部门
        EnterpriseDepartment department = new EnterpriseDepartment();
        department.setEnterpriseId(dto.getEnterpriseId());
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setManagerId(dto.getManagerId());
        department.setCreateTime(new Date());
        department.setUpdateTime(new Date());

        departmentDao.save(department);

        // 如果指定了部门主管，将该用户设为部门主管角色
        if (dto.getManagerId() != null) {
            updateDepartmentManager(department.getId(), dto.getManagerId(), userId);
        }

        return department;
    }

    @Override
    public EnterpriseDepartment updateDepartment(EnterpriseDepartmentDTO dto, Long userId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(dto.getId());
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查权限：只有所有者、管理员或部门主管可以修改部门信息
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        boolean isOwnerOrAdmin = currentMember.getRole() <= EnterpriseRole.ADMIN.code;
        boolean isDepartmentManager = currentMember.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code
                && userId.equals(department.getManagerId());

        if (!isOwnerOrAdmin && !isDepartmentManager) {
            throw new BusinessException("您没有权限修改部门信息");
        }

        // 检查部门名称是否已存在（排除当前部门）
        if (!department.getName().equals(dto.getName()) &&
                departmentDao.nameExists(department.getEnterpriseId(), dto.getName(), department.getId())) {
            throw new BusinessException("部门名称已存在");
        }

        // 更新部门信息
        department.setName(dto.getName());
        department.setDescription(dto.getDescription());
        department.setUpdateTime(new Date());

        // 只有所有者和管理员可以修改部门主管
        if (isOwnerOrAdmin && dto.getManagerId() != null && !dto.getManagerId().equals(department.getManagerId())) {
            department.setManagerId(dto.getManagerId());
            // 更新部门主管角色
            updateDepartmentManager(department.getId(), dto.getManagerId(), userId);
        }

        departmentDao.update(department);
        return department;
    }

    @Override
    public void deleteDepartment(Long id, Long userId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查权限：只有所有者和管理员可以删除部门
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (currentMember == null || currentMember.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限删除部门");
        }

        // 将部门成员的部门ID设为null
        List<EnterpriseMember> departmentMembers = memberDao.findByDepartmentId(id);
        for (EnterpriseMember member : departmentMembers) {
            // 如果是部门主管，将角色改为普通成员
            if (member.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code) {
                member.setRole(EnterpriseRole.MEMBER.code);
            }
            member.setDepartmentId(null);
            memberDao.update(member);
        }

        // 删除部门
        departmentDao.delete(id);
    }

    @Override
    public EnterpriseDepartmentVO getDepartmentById(Long id, Long userId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(id);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查权限：必须是企业成员才能查看部门信息
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        return convertToVO(department);
    }

    @Override
    public List<EnterpriseDepartmentVO> listDepartments(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：必须是企业成员才能查看部门列表
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        // 获取所有部门
        List<EnterpriseDepartment> departments = departmentDao.findByEnterpriseId(enterpriseId);

        // 转换为VO对象
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseDepartmentVO> listManagedDepartments(Long userId) {
        // 获取用户管理的部门
        List<EnterpriseDepartment> departments = departmentDao.findByManagerId(userId);

        // 转换为VO对象
        return departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseMemberVO> getDepartmentMembers(Long departmentId, Long userId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查权限：必须是企业成员才能查看部门成员
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        // 获取部门成员
        List<EnterpriseMember> members = memberDao.findByDepartmentId(departmentId);

        // 转换为VO对象
        return members.stream()
                .map(this::convertMemberToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void addMember(Long departmentId, Long userId, Long operatorId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 验证企业成员存在
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (member == null) {
            throw new BusinessException("用户不是企业成员");
        }

        // 检查权限：所有者、管理员或部门主管可以添加成员
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), operatorId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        boolean isOwnerOrAdmin = currentMember.getRole() <= EnterpriseRole.ADMIN.code;
        boolean isDepartmentManager = currentMember.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code
                && operatorId.equals(department.getManagerId());

        if (!isOwnerOrAdmin && !isDepartmentManager) {
            throw new BusinessException("您没有权限添加部门成员");
        }

        // 检查用户是否已经在部门中
        if (member.getDepartmentId() != null && member.getDepartmentId().equals(departmentId)) {
            throw new BusinessException("用户已经是部门成员");
        }

        // 更新成员部门
        member.setDepartmentId(departmentId);
        memberDao.update(member);
    }

    @Override
    public void removeMember(Long departmentId, Long userId, Long operatorId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 验证企业成员存在
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (member == null) {
            throw new BusinessException("用户不是企业成员");
        }

        // 检查是否是部门成员
        if (member.getDepartmentId() == null || !member.getDepartmentId().equals(departmentId)) {
            throw new BusinessException("用户不是部门成员");
        }

        // 检查权限：所有者、管理员或部门主管可以移除成员
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), operatorId);
        if (currentMember == null) {
            throw new BusinessException("您不是企业成员");
        }

        boolean isOwnerOrAdmin = currentMember.getRole() <= EnterpriseRole.ADMIN.code;
        boolean isDepartmentManager = currentMember.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code
                && operatorId.equals(department.getManagerId());

        if (!isOwnerOrAdmin && !isDepartmentManager) {
            throw new BusinessException("您没有权限移除部门成员");
        }

        // 不能移除部门主管
        if (userId.equals(department.getManagerId())) {
            throw new BusinessException("不能将部门主管从部门中移除");
        }

        // 移除成员部门
        member.setDepartmentId(null);
        memberDao.update(member);
    }

    @Override
    public void changeManager(Long departmentId, Long newManagerId, Long userId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 检查权限：只有所有者和管理员可以修改部门主管
        EnterpriseMember currentMember = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), userId);
        if (currentMember == null || currentMember.getRole() > EnterpriseRole.ADMIN.code) {
            throw new BusinessException("您没有权限修改部门主管");
        }

        // 验证新主管是企业成员
        EnterpriseMember newManager = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), newManagerId);
        if (newManager == null) {
            throw new BusinessException("新主管不是企业成员");
        }

        // 如果有旧主管，将其角色改为普通成员
        if (department.getManagerId() != null) {
            EnterpriseMember oldManager = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), department.getManagerId());
            if (oldManager != null && oldManager.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code) {
                oldManager.setRole(EnterpriseRole.MEMBER.code);
                memberDao.update(oldManager);
            }
        }

        // 更新部门主管
        department.setManagerId(newManagerId);
        department.setUpdateTime(new Date());
        departmentDao.update(department);

        // 将新主管角色设为部门主管
        newManager.setRole(EnterpriseRole.DEPARTMENT_MANAGER.code);
        newManager.setDepartmentId(departmentId);
        memberDao.update(newManager);
    }

    // 辅助方法：将EnterpriseDepartment转换为VO
    private EnterpriseDepartmentVO convertToVO(EnterpriseDepartment department) {
        EnterpriseDepartmentVO vo = new EnterpriseDepartmentVO();
        vo.setId(department.getId());
        vo.setEnterpriseId(department.getEnterpriseId());
        vo.setName(department.getName());
        vo.setDescription(department.getDescription());
        vo.setManagerId(department.getManagerId());
        vo.setCreateTime(department.getCreateTime());
        vo.setUpdateTime(department.getUpdateTime());

        // 获取成员数量
        List<EnterpriseMember> members = memberDao.findByDepartmentId(department.getId());
        vo.setMemberCount(members.size());

        // 获取部门主管信息
        if (department.getManagerId() != null) {
            User manager = userDao.findById(department.getManagerId());
            if (manager != null) {
                vo.setManagerName(manager.getUsername());
                vo.setManagerAvatar(manager.getAvatar());
            }
        }

        return vo;
    }

    // 辅助方法：将EnterpriseMember转换为VO
    private EnterpriseMemberVO convertMemberToVO(EnterpriseMember member) {
        EnterpriseMemberVO vo = new EnterpriseMemberVO();
        vo.setId(member.getId());
        vo.setEnterpriseId(member.getEnterpriseId());
        vo.setUserId(member.getUserId());
        vo.setRole(member.getRole());
        vo.setRoleName(EnterpriseRole.getNameByCode(member.getRole()));
        vo.setDepartmentId(member.getDepartmentId());
        vo.setJoinTime(member.getJoinTime());

        // 获取用户信息
        User user = userDao.findById(member.getUserId());
        if (user != null) {
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            vo.setEmail(user.getEmail());
        }

        // 获取部门信息
        if (member.getDepartmentId() != null) {
            EnterpriseDepartment department = departmentDao.findById(member.getDepartmentId());
            if (department != null) {
                vo.setDepartmentName(department.getName());
            }
        }

        return vo;
    }

    // 辅助方法：更新部门主管
    private void updateDepartmentManager(Long departmentId, Long managerId, Long operatorId) {
        // 验证部门存在
        EnterpriseDepartment department = departmentDao.findById(departmentId);
        if (department == null) {
            throw new BusinessException("部门不存在");
        }

        // 验证新主管是企业成员
        EnterpriseMember newManager = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), managerId);
        if (newManager == null) {
            throw new BusinessException("新主管不是企业成员");
        }

        // 如果有旧主管，将其角色改为普通成员
        if (department.getManagerId() != null && !department.getManagerId().equals(managerId)) {
            EnterpriseMember oldManager = memberDao.findByEnterpriseAndUser(department.getEnterpriseId(), department.getManagerId());
            if (oldManager != null && oldManager.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code) {
                oldManager.setRole(EnterpriseRole.MEMBER.code);
                memberDao.update(oldManager);
            }
        }

        // 将新主管角色设为部门主管
        newManager.setRole(EnterpriseRole.DEPARTMENT_MANAGER.code);
        newManager.setDepartmentId(departmentId);
        memberDao.update(newManager);
    }
}