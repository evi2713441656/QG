package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.KnowledgeMember;
import java.util.List;

/**
 * @author evi
 */
public interface KnowledgeMemberDao {

    KnowledgeMember findById(Long id);

    KnowledgeMember findByKnowledgeAndUser(Long knowledgeId, Long userId);

    List<KnowledgeMember> findByKnowledgeId(Long knowledgeId);

    List<KnowledgeMember> findByUserId(Long userId);

    boolean exists(Long knowledgeId, Long userId);

    void save(KnowledgeMember member);

    void update(KnowledgeMember member);

    void delete(Long id);

    void deleteByKnowledgeAndUser(Long knowledgeId, Long userId);

    int countMembers(Long knowledgeId);

}