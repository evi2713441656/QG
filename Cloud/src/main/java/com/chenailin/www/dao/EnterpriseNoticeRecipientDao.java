package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.EnterpriseNoticeRecipient;
import java.util.List;

/**
 * Data Access Object interface for enterprise notice recipient operations
 * @author evi
 */
public interface EnterpriseNoticeRecipientDao {
    EnterpriseNoticeRecipient findById(Long id);
    List<EnterpriseNoticeRecipient> findByNoticeId(Long noticeId);
    List<EnterpriseNoticeRecipient> findByUserId(Long userId, int limit, int offset);
    List<EnterpriseNoticeRecipient> findUnreadByUserId(Long userId, int limit, int offset);
    int countUnreadByUserId(Long userId);
    void save(EnterpriseNoticeRecipient recipient);
//    void batchSave(List<EnterpriseNoticeRecipient> recipients);
    void update(EnterpriseNoticeRecipient recipient);
    void markAsRead(Long noticeId, Long userId);
    void markAllAsRead(Long userId);
    void delete(Long id);
    void deleteByNoticeId(Long noticeId);
}