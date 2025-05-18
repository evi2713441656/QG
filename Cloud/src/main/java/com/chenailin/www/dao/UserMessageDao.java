package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.UserMessage;
import java.util.List;

/**
 * Data Access Object interface for user message operations
 * @author evi
 */
public interface UserMessageDao {
    UserMessage findById(Long id);
    List<UserMessage> findBySenderId(Long senderId, int limit, int offset);
    List<UserMessage> findByRecipientId(Long recipientId, int limit, int offset);
    List<UserMessage> findUnreadByRecipientId(Long recipientId, int limit, int offset);
    int countUnreadByRecipientId(Long recipientId);
    List<UserMessage> findConversation(Long userId1, Long userId2, int limit, int offset);
    List<UserMessage> findNewConversationMessages(Long userId1, Long userId2, Long lastMessageId);
    void save(UserMessage message);
    void markAsRead(Long messageId);
    void markAllAsRead(Long senderId, Long recipientId);
    void delete(Long id);
    void deleteConversation(Long userId1, Long userId2);
}