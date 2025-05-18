package com.chenailin.www.service;

import com.chenailin.www.model.vo.NotificationVO;

import java.util.List;

/**
 * Notification Service Interface
 * @author evi
 */
public interface NotificationService {
    NotificationVO createNotification(Long userId, String type, String content, Long relatedId);
    void createBatchNotifications(List<Long> userIds, String type, String content, Long relatedId);
    NotificationVO getNotificationById(Long id, Long userId);
    List<NotificationVO> getAllNotifications(Long userId, int page, int size);
    int countAllNotifications(Long userId);
    List<NotificationVO> getNotificationsByType(Long userId, String type, int page, int size);
    int countNotificationsByType(Long userId, String type);
    List<NotificationVO> getUnreadNotifications(Long userId, int page, int size);
    int countUnreadNotifications(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    void markAllAsReadByType(Long userId, String type);
    void deleteNotification(Long notificationId, Long userId);
    void deleteAllNotifications(Long userId);
    void deleteReadNotifications(Long userId);
    List<NotificationVO> getRecentNotifications(Long userId, int limit);
    void processNotificationAction(Long notificationId, Long userId, String action);
    void createArticleNotification(Long articleId, Long authorId, Long actorId, String type, String content);
    void createCommentNotification(Long commentId, Long authorId, Long actorId, String type, String content);
    void createEnterpriseNotification(Long enterpriseId, Long userId, String type, String content, Long actorId);
    void createSystemNotification(String content, Long relatedId);
    void deleteOldNotifications(Long userId, int days);
}