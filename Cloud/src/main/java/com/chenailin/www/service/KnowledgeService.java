package com.chenailin.www.service;

import com.chenailin.www.model.dto.KnowledgeBaseDTO;
import com.chenailin.www.model.dto.KnowledgeMemberDTO;
import com.chenailin.www.model.pojo.KnowledgeBase;
import com.chenailin.www.model.pojo.KnowledgeMember;
import com.chenailin.www.model.vo.KnowledgeBaseVO;

import java.util.List;

/**
 * @author evi
 */
public interface KnowledgeService {
    // 知识库CRUD
    KnowledgeBase createBase(KnowledgeBaseDTO dto, Long creatorId);
    KnowledgeBaseVO getBaseById(Long id, Long userId);
    List<KnowledgeBaseVO> listUserBases(Long userId);
    List<KnowledgeBaseVO> listPublicBases();
    KnowledgeBase updateBase(Long id, KnowledgeBaseDTO dto, Long userId);
    void deleteBase(Long id, Long userId);

    // 成员管理
    void addMember(KnowledgeMemberDTO dto, Long operatorId);
    void removeMember(Long knowledgeId, Long memberId, Long operatorId);
    void updateMemberRole(KnowledgeMemberDTO dto, Long operatorId);
    List<KnowledgeMember> listMembers(Long knowledgeId, Long userId);
}