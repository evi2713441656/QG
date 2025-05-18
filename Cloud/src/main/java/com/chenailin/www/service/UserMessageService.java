package com.chenailin.www.service;

import com.chenailin.www.model.vo.UserMessageVO;

import java.util.List;

/**
 * User Message Service Interface
 * @author evi
 */
public interface UserMessageService {
    UserMessageVO sendMessage(Long senderId, Long recipientId, String content);
    UserMessageVO getMessageById(Long messageId, Long userId);
    List<UserMessageVO> getConversation(Long userId1, Long userId2, int page, int size);
    int countConversationMessages(Long userId1, Long userId2);
    List<UserMessageVO> getNewConversationMessages(Long userId1, Long userId2, Long lastMessageId);
    List<UserMessageVO> getAllMessages(Long userId, int page, int size);
    int countAllMessages(Long userId);
    List<UserMessageVO> getMessagesByType(Long userId, String type, int page, int size);
    int countMessagesByType(Long userId, String type);
    List<UserMessageVO> getUnreadMessages(Long userId);
    int countUnreadMessages(Long userId);
    void markAsRead(Long messageId, Long userId);
    void markConversationAsRead(Long userId, Long otherUserId);
    void markAllAsRead(Long userId);
    void deleteMessage(Long messageId, Long userId);
    void deleteConversation(Long userId1, Long userId2);
}