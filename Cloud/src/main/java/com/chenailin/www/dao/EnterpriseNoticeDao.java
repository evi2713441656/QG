package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.EnterpriseNotice;
import java.util.List;

/**
 * Data Access Object interface for enterprise notice operations
 * @author evi
 */
public interface EnterpriseNoticeDao {
    EnterpriseNotice findById(Long id);
    List<EnterpriseNotice> findByEnterpriseId(Long enterpriseId, int limit, int offset);
    int countByEnterpriseId(Long enterpriseId);
    List<EnterpriseNotice> findByPublisherId(Long userId, int limit, int offset);
    void save(EnterpriseNotice notice);
    void update(EnterpriseNotice notice);
    void delete(Long id);
    void deleteByEnterpriseId(Long enterpriseId);
}