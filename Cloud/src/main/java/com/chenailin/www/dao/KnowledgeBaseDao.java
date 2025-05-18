package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.KnowledgeBase;

import java.util.List;

/**
 * @author evi
 */
public interface KnowledgeBaseDao {

    KnowledgeBase findById(Long id);

    List<KnowledgeBase> findByUserId(Long userId);

    List<KnowledgeBase> findPublicBases();

    void save(KnowledgeBase knowledgeBase);

    void update(KnowledgeBase knowledgeBase);

    void delete(Long id);

    boolean checkNameExists(String name, Long excludeId);

//    List<KnowledgeBase> searchByKeyword(String keyword, int limit, int offset);
}