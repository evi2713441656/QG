package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.*;
import com.chenailin.www.daoimpl.*;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.EnterpriseDTO;
import com.chenailin.www.model.dto.EnterpriseMemberDTO;
import com.chenailin.www.model.enums.EnterpriseRole;
import com.chenailin.www.model.pojo.Enterprise;
import com.chenailin.www.model.pojo.EnterpriseMember;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.vo.EnterpriseMemberVO;
import com.chenailin.www.model.vo.EnterpriseVO;
import com.chenailin.www.service.EnterpriseService;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author evi
 */
public class EnterpriseServiceImpl implements EnterpriseService {
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();
    private final EnterpriseMemberDao enterpriseMemberDao = new EnterpriseMemberDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final EnterpriseDepartmentDao departmentDao = new EnterpriseDepartmentDaoImpl();

    @Override
    public Enterprise createEnterprise(EnterpriseDTO dto, Long creatorId) {
        // 验证企业名称是否已存在
        if (enterpriseDao.exists(dto.getName())) {
            throw new BusinessException("企业名称已存在");
        }

        // 创建企业
        Enterprise enterprise = new Enterprise();
        enterprise.setName(dto.getName());
        enterprise.setCreatorId(creatorId);
        enterprise.setCreateTime(new Date());
        enterprise.setUpdateTime(new Date());

        enterpriseDao.save(enterprise);

        // 自动添加创建者为所有者
        EnterpriseMember owner = new EnterpriseMember();
        owner.setEnterpriseId(enterprise.getId());
        owner.setUserId(creatorId);
        owner.setRole(EnterpriseRole.OWNER.code);
        owner.setJoinTime(new Date());
        enterpriseMemberDao.save(owner);

        return enterprise;
    }

    @Override
    public Enterprise updateEnterprise(EnterpriseDTO dto, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(dto.getId());
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：只有所有者可以修改企业信息
        EnterpriseMember member = enterpriseMemberDao.findByEnterpriseAndUser(dto.getId(), userId);
        if (member == null || member.getRole() != EnterpriseRole.OWNER.code) {
            throw new BusinessException("需要所有者权限");
        }

        // 验证企业名称是否已存在（排除当前企业）
        if (!enterprise.getName().equals(dto.getName()) && enterpriseDao.exists(dto.getName())) {
            throw new BusinessException("企业名称已存在");
        }

        // 更新企业信息
        enterprise.setName(dto.getName());
        enterprise.setUpdateTime(new Date());

        enterpriseDao.update(enterprise);
        return enterprise;
    }

    @Override
    public void deleteEnterprise(Long id, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(id);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：只有所有者可以删除企业
        EnterpriseMember member = enterpriseMemberDao.findByEnterpriseAndUser(id, userId);
        if (member == null || member.getRole() != EnterpriseRole.OWNER.code) {
            throw new BusinessException("需要所有者权限");
        }

        // 删除企业相关数据
        // 1. 删除部门
        departmentDao.deleteByEnterpriseId(id);

        // 2. 删除成员
        enterpriseMemberDao.deleteByEnterpriseId(id);

        // 3. 删除企业
        enterpriseDao.delete(id);
    }

    @Override
    public EnterpriseVO getEnterpriseById(Long id, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(id);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：必须是企业成员才能查看企业信息
        EnterpriseMember member = enterpriseMemberDao.findByEnterpriseAndUser(id, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        return convertToVO(enterprise, userId);
    }

    @Override
    public List<EnterpriseVO> getUserEnterprises(Long userId) {
        // 获取用户所在的所有企业
        List<Enterprise> enterprises = enterpriseDao.findByUserId(userId);

        // 转换为VO对象
        return enterprises.stream()
                .map(enterprise -> convertToVO(enterprise, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseVO> getCreatedEnterprises(Long creatorId) {
        // 获取用户创建的所有企业
        List<Enterprise> enterprises = enterpriseDao.findByCreatorId(creatorId);

        // 转换为VO对象
        return enterprises.stream()
                .map(enterprise -> convertToVO(enterprise, creatorId))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseVO> getOwnedEnterprises(Long userId) {
        // 获取用户创建的所有企业
        List<Enterprise> enterprises = enterpriseDao.findByCreatorId(userId);

        // 转换为VO对象
        return enterprises.stream()
                .map(enterprise -> convertToVO(enterprise, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseMemberVO> getEnterpriseMembers(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：必须是企业成员才能查看成员列表
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (currentMember == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 获取所有成员
        List<EnterpriseMember> members = enterpriseMemberDao.findByEnterpriseId(enterpriseId);

        // 转换为VO对象
        return members.stream()
                .map(this::convertMemberToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void inviteMember(Long enterpriseId, EnterpriseMemberDTO dto, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查权限：只有所有者和管理员可以邀请成员
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (currentMember == null || (currentMember.getRole() > EnterpriseRole.ADMIN.code)) {
            throw new BusinessException("您没有权限邀请成员");
        }

        // 检查被邀请用户是否存在
        User invitedUser = userDao.findByEmail(dto.getEmail());
        if (invitedUser == null) {
            throw new BusinessException("被邀请用户不存在");
        }

        // 检查用户是否已经是成员
        EnterpriseMember existingMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, invitedUser.getId());
        if (existingMember != null) {
            throw new BusinessException("该用户已经是企业成员");
        }

        // 直接添加成员（简化版本，不使用邀请码）
        EnterpriseMember member = new EnterpriseMember();
        member.setEnterpriseId(enterpriseId);
        member.setUserId(invitedUser.getId());
        member.setRole(dto.getRole());
        member.setDepartmentId(dto.getDepartmentId());
        member.setJoinTime(new Date());
        enterpriseMemberDao.save(member);

        // 创建系统通知
        createMemberInviteNotification(enterpriseId, invitedUser.getId(), userId);
    }

    @Override
    public void joinEnterprise(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查用户是否已经是成员
        EnterpriseMember existingMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (existingMember != null) {
            throw new BusinessException("您已经是该企业的成员");
        }

        // 添加用户为普通成员
        EnterpriseMember member = new EnterpriseMember();
        member.setEnterpriseId(enterpriseId);
        member.setUserId(userId);
        member.setRole(EnterpriseRole.MEMBER.code);
        member.setJoinTime(new Date());
        enterpriseMemberDao.save(member);
    }

    @Override
    public void updateMemberRole(Long memberId, EnterpriseMemberDTO dto, Long userId) {
        // 验证成员记录存在
        EnterpriseMember member = enterpriseMemberDao.findById(memberId);
        if (member == null) {
            throw new BusinessException("成员记录不存在");
        }

        // 检查权限：只有所有者可以修改角色
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(member.getEnterpriseId(), userId);
        if (currentMember == null || currentMember.getRole() != EnterpriseRole.OWNER.code) {
            throw new BusinessException("您没有权限修改成员角色");
        }

        // 不能修改所有者角色
        if (member.getRole() == EnterpriseRole.OWNER.code) {
            throw new BusinessException("不能修改所有者角色");
        }

        // 不能自己修改自己的角色
        if (member.getUserId().equals(userId)) {
            throw new BusinessException("不能修改自己的角色");
        }

        // 更新角色
        member.setRole(dto.getRole());

        // 如果更新为部门主管，设置部门ID
        if (dto.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code && dto.getDepartmentId() != null) {
            member.setDepartmentId(dto.getDepartmentId());
        }

        enterpriseMemberDao.update(member);
    }

    @Override
    public void updateMemberDepartment(Long memberId, Long departmentId, Long userId) {
        // 验证成员记录存在
        EnterpriseMember member = enterpriseMemberDao.findById(memberId);
        if (member == null) {
            throw new BusinessException("成员记录不存在");
        }

        // 检查权限：所有者、管理员或部门主管可以修改部门
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(member.getEnterpriseId(), userId);
        if (currentMember == null || currentMember.getRole() > EnterpriseRole.DEPARTMENT_MANAGER.code) {
            throw new BusinessException("您没有权限修改成员部门");
        }

        // 部门主管只能管理自己的部门成员
        if (currentMember.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code &&
                !currentMember.getDepartmentId().equals(member.getDepartmentId())) {
            throw new BusinessException("您只能管理自己部门的成员");
        }

        // 更新部门
        member.setDepartmentId(departmentId);
        enterpriseMemberDao.update(member);
    }

    @Override
    public void leaveEnterprise(Long enterpriseId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查用户是否是企业成员
        EnterpriseMember member = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 所有者不能直接离开企业，需要先转移所有权
        if (member.getRole() == EnterpriseRole.OWNER.code) {
            throw new BusinessException("企业所有者不能直接离开企业，请先转移所有权");
        }

        // 离开企业
        enterpriseMemberDao.delete(member.getId());
    }

    @Override
    public void removeMember(Long enterpriseId, Long memberId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查要移除的用户是否是企业成员
        EnterpriseMember memberToRemove = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, memberId);
        if (memberToRemove == null) {
            throw new BusinessException("要移除的用户不是企业成员");
        }

        // 检查操作者权限
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (currentMember == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 检查权限规则：
        // 1. 所有者可以移除任何人（除了自己）
        // 2. 管理员可以移除普通成员，但不能移除所有者或其他管理员
        // 3. 部门主管可以移除自己部门的普通成员
        // 4. 普通成员不能移除任何人

        if (currentMember.getRole() == EnterpriseRole.OWNER.code) {
            // 所有者不能移除自己
            if (memberId.equals(userId)) {
                throw new BusinessException("企业所有者不能移除自己，请先转移所有权");
            }
        } else if (currentMember.getRole() == EnterpriseRole.ADMIN.code) {
            // 管理员只能移除普通成员和部门主管
            if (memberToRemove.getRole() <= EnterpriseRole.ADMIN.code) {
                throw new BusinessException("您没有权限移除此成员");
            }
        } else if (currentMember.getRole() == EnterpriseRole.DEPARTMENT_MANAGER.code) {
            // 部门主管只能移除自己部门的普通成员
            if (memberToRemove.getRole() != EnterpriseRole.MEMBER.code ||
                    !currentMember.getDepartmentId().equals(memberToRemove.getDepartmentId())) {
                throw new BusinessException("您没有权限移除此成员");
            }
        } else {
            // 普通成员不能移除任何人
            throw new BusinessException("您没有权限移除成员");
        }

        // 移除成员
        enterpriseMemberDao.delete(memberToRemove.getId());
    }

    @Override
    public void transferOwnership(Long enterpriseId, Long newOwnerId, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 检查当前用户权限
        EnterpriseMember currentMember = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, userId);
        if (currentMember == null || currentMember.getRole() != EnterpriseRole.OWNER.code) {
            throw new BusinessException("只有企业所有者才能转移所有权");
        }

        // 检查新所有者是否是企业成员
        EnterpriseMember newOwner = enterpriseMemberDao.findByEnterpriseAndUser(enterpriseId, newOwnerId);
        if (newOwner == null) {
            throw new BusinessException("新所有者不是企业成员");
        }

        // 不能将所有权转移给自己
        if (newOwnerId.equals(userId)) {
            throw new BusinessException("不能将所有权转移给自己");
        }

        // 将当前所有者角色降为管理员
        currentMember.setRole(EnterpriseRole.ADMIN.code);
        enterpriseMemberDao.update(currentMember);

        // 将新所有者角色升为所有者
        newOwner.setRole(EnterpriseRole.OWNER.code);
        enterpriseMemberDao.update(newOwner);

        // 更新企业创建者ID
        enterprise.setCreatorId(newOwnerId);
        enterprise.setUpdateTime(new Date());
        enterpriseDao.update(enterprise);
    }

    // 辅助方法：将Enterprise转换为EnterpriseVO
    private EnterpriseVO convertToVO(Enterprise enterprise, Long userId) {
        EnterpriseVO vo = new EnterpriseVO();
        vo.setId(enterprise.getId());
        vo.setName(enterprise.getName());
        vo.setCreatorId(enterprise.getCreatorId());
        vo.setCreateTime(enterprise.getCreateTime());
        vo.setUpdateTime(enterprise.getUpdateTime());

        // 获取创建者信息
        User creator = userDao.findById(enterprise.getCreatorId());
        if (creator != null) {
            vo.setCreatorName(creator.getUsername());
            vo.setCreatorAvatar(creator.getAvatar());
        }

        // 设置成员数量
        vo.setMemberCount(enterpriseMemberDao.countMembers(enterprise.getId()));

        // 设置当前用户角色
        EnterpriseMember member = enterpriseMemberDao.findByEnterpriseAndUser(enterprise.getId(), userId);
        if (member != null) {
            vo.setCurrentUserRole(member.getRole());
            vo.setRoleName(EnterpriseRole.getNameByCode(member.getRole()));
        }

        return vo;
    }

    // 辅助方法：将EnterpriseMember转换为EnterpriseMemberVO
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
            vo.setDepartmentName(departmentDao.findById(member.getDepartmentId()).getName());
        }

        return vo;
    }

    // 辅助方法：创建成员邀请通知
    private void createMemberInviteNotification(Long enterpriseId, Long userId, Long inviterId) {
        // 获取企业信息
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);

        // 获取邀请者信息
        User inviter = userDao.findById(inviterId);

        // 通知内容
        String content = String.format("%s邀请您加入企业%s", inviter.getUsername(), enterprise.getName());

        // 这里需要调用通知服务创建通知
        // notificationService.createNotification(userId, "enterprise_invite", content, enterpriseId);
        // 由于无法直接调用通知服务，可以在此处记录日志
        System.out.println("创建企业邀请通知: " + content + " 发送给用户ID: " + userId);
    }
}