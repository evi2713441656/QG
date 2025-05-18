package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.KnowledgeBaseDao;
import com.chenailin.www.dao.KnowledgeMemberDao;
import com.chenailin.www.daoimpl.KnowledgeBaseDaoImpl;
import com.chenailin.www.daoimpl.KnowledgeMemberDaoImpl;
import com.chenailin.www.model.dto.*;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.enums.KnowledgeRole;
import com.chenailin.www.model.vo.KnowledgeBaseVO;
import com.chenailin.www.service.KnowledgeService;
import com.chenailin.www.util.KnowledgeAuthUtil;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author evi
 */
public class KnowledgeServiceImpl implements KnowledgeService {
    private final KnowledgeBaseDao baseDao = new KnowledgeBaseDaoImpl();
    private final KnowledgeMemberDao memberDao = new KnowledgeMemberDaoImpl();

    @Override
    public KnowledgeBase createBase(KnowledgeBaseDTO dto, Long creatorId) {
        // 验证名称是否已存在
        if (baseDao.checkNameExists(dto.getName(), null)) {
            throw new RuntimeException("知识库名称已存在");
        }

        KnowledgeBase base = new KnowledgeBase();
        base.setName(dto.getName());
        base.setDescription(dto.getDescription());
        base.setIsPublic(dto.getIsPublic());
        base.setCreatorId(creatorId);
        base.setCreateTime(new Date());
        base.setUpdateTime(new Date());

        System.out.println(base.getName());
        System.out.println(base.getDescription());
        System.out.println(base.getCoverUrl());
        System.out.println(base.getIsPublic());
        System.out.println(base.getCreatorId());


        baseDao.save(base);
        // 自动添加创建者为所有者
        KnowledgeMember owner = new KnowledgeMember();
        owner.setKnowledgeId(base.getId());
        owner.setUserId(creatorId);
        owner.setRole(KnowledgeRole.OWNER.code);
        owner.setJoinTime(new Date());
        memberDao.save(owner);

        return base;
    }

    @Override
    public KnowledgeBaseVO getBaseById(Long id, Long userId) {
        KnowledgeBase base = baseDao.findById(id);
        if (base == null) {
            System.out.println("知识库不存在");
            throw new RuntimeException("知识库不存在");
        }

        // 检查访问权限
        if (!base.getIsPublic() && !memberDao.exists(id, userId)) {
            System.out.println("无权限访问该知识库");
            throw new RuntimeException("无权限访问该知识库");
        }

        return convertToVO(base, userId);
    }

    @Override
    public List<KnowledgeBaseVO> listUserBases(Long userId) {
        List<KnowledgeBase> knowledgeBases = baseDao.findByUserId(userId);
        return knowledgeBases.stream()
                .map(base -> convertToVO(base, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<KnowledgeBaseVO> listPublicBases() {
        List<KnowledgeBase> publicBases = baseDao.findPublicBases();
        return publicBases.stream()
                .map(base -> convertToVO(base, null))
                .collect(Collectors.toList());
    }

    @Override
    public KnowledgeBase updateBase(Long id, KnowledgeBaseDTO dto, Long userId) {
        // 根据传入的知识库ID查找对应的知识库
        KnowledgeBase base = baseDao.findById(id);
        if (base == null) {
            System.out.println("知识库不存在");
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有所有者可以更新知识库
        KnowledgeMember member = memberDao.findByKnowledgeAndUser(id, userId);
        if (member == null || member.getRole() > KnowledgeRole.OWNER.code) {
            System.out.println("需要所有者权限");
            throw new RuntimeException("需要所有者权限");
        }

        // 验证名称是否已存在（排除当前知识库）
        if (baseDao.checkNameExists(dto.getName(), id)) {
            System.out.println("知识库名称已存在");
            throw new RuntimeException("知识库名称已存在");
        }

        // 更新知识库信息
        base.setName(dto.getName());
        base.setDescription(dto.getDescription());
        base.setIsPublic(dto.getIsPublic());
        base.setUpdateTime(new Date());
        System.out.println(base.getId());
        System.out.println(base.getName());
        System.out.println(base.getDescription());
        System.out.println(base.getIsPublic());
        // 调用DAO层的更新方法
        baseDao.update(base);

        return base;
    }

    @Override
    public void deleteBase(Long id, Long userId) {
        KnowledgeBase base = baseDao.findById(id);
        if (base == null) {
            throw new RuntimeException("知识库不存在");
        }

        // 检查权限：只有所有者可以删除知识库
        KnowledgeMember member = memberDao.findByKnowledgeAndUser(id, userId);
        if (member == null || member.getRole() > KnowledgeRole.OWNER.code) {
            throw new RuntimeException("需要所有者权限");
        }

        baseDao.delete(id);
    }

    @Override
    public void addMember(KnowledgeMemberDTO dto, Long operatorId) {
        // 检查操作权限：所有者或管理员可以添加成员
        KnowledgeAuthUtil.checkPermission(dto.getKnowledgeId(), operatorId, KnowledgeRole.ADMIN);

//        // 检查用户是否存在
//        User user = userService.getUserById(dto.getUserId());
//        if (user == null) {
//            throw new RuntimeException("用户不存在");
//        }

        // 检查是否已加入
        if (memberDao.exists(dto.getKnowledgeId(), dto.getUserId())) {
            throw new RuntimeException("用户已加入该知识库");
        }

        // 添加成员
        KnowledgeMember member = new KnowledgeMember();
        member.setKnowledgeId(dto.getKnowledgeId());
        member.setUserId(dto.getUserId());
        member.setRole(dto.getRole());
        member.setJoinTime(new Date());
        memberDao.save(member);
    }

    @Override
    public void removeMember(Long knowledgeId, Long memberId, Long operatorId) {
        // 检查操作权限：所有者可以移除任何成员，管理员只能移除普通成员
        KnowledgeMember operator = memberDao.findByKnowledgeAndUser(knowledgeId, operatorId);
        if (operator == null || operator.getRole() > KnowledgeRole.ADMIN.code) {
            throw new RuntimeException("需要管理员权限");
        }

        KnowledgeMember member = memberDao.findById(memberId);
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        // 不能移除自己
        if (member.getUserId().equals(operatorId)) {
            throw new RuntimeException("不能移除自己");
        }

        // 管理员不能移除其他管理员和所有者
        if (operator.getRole() == KnowledgeRole.ADMIN.code && member.getRole() <= KnowledgeRole.ADMIN.code) {
            throw new RuntimeException("权限不足，无法移除该成员");
        }

        memberDao.delete(memberId);
    }

    @Override
    public void updateMemberRole(KnowledgeMemberDTO dto, Long operatorId) {
        // 检查操作权限：所有者可以修改任何成员角色，管理员只能修改普通成员角色
        KnowledgeMember operator = memberDao.findByKnowledgeAndUser(dto.getKnowledgeId(), operatorId);
        if (operator == null || operator.getRole() > KnowledgeRole.ADMIN.code) {
            throw new RuntimeException("需要管理员权限");
        }

        KnowledgeMember member = memberDao.findByKnowledgeAndUser(dto.getKnowledgeId(), dto.getUserId());
        if (member == null) {
            throw new RuntimeException("成员不存在");
        }

        // 不能修改自己的角色
        if (member.getUserId().equals(operatorId)) {
            throw new RuntimeException("不能修改自己的角色");
        }

        // 管理员不能提升/降低其他管理员和所有者的角色
        if (operator.getRole() == KnowledgeRole.ADMIN.code &&
                (member.getRole() <= KnowledgeRole.ADMIN.code || dto.getRole() <= KnowledgeRole.ADMIN.code)) {
            throw new RuntimeException("权限不足，无法修改该成员角色");
        }

        // 更新角色
        member.setRole(dto.getRole());
        memberDao.update(member);
    }

    @Override
    public List<KnowledgeMember> listMembers(Long knowledgeId, Long userId) {
        return Collections.emptyList();
    }

//    @Override
//    public List<KnowledgeMember> listMembers(Long knowledgeId, Long userId) {
//        // 检查访问权限：只有成员可以查看成员列表
//        KnowledgeAuthUtil.checkPermission(knowledgeId, userId, KnowledgeRole.MEMBER);
//        return memberDao.listByKnowledge(knowledgeId);
//    }

//？？？
    private KnowledgeBaseVO convertToVO(KnowledgeBase base, Long userId) {
        KnowledgeBaseVO vo = new KnowledgeBaseVO();
        vo.setId(base.getId());
        vo.setName(base.getName());
        vo.setDescription(base.getDescription());
        vo.setCoverUrl(base.getCoverUrl());
        vo.setIsPublic(base.getIsPublic());
        vo.setCreateTime(base.getCreateTime());

        // 设置当前用户角色
        KnowledgeMember member = memberDao.findByKnowledgeAndUser(base.getId(), userId);
//        if (member != null) {
//            vo.setCurrentUserRole(KnowledgeRole.getCode(member.getRole()));
//        }
        //???

        return vo;
    }

    // 其他方法实现...
}