package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.Notification;
import java.util.List;

/**
 * Data Access Object interface for notification operations
 * @author evi
 */
public interface NotificationDao {

    Notification findById(Long id);
    List<Notification> findByUserId(Long userId, int limit, int offset);
    List<Notification> findByUserIdAndType(Long userId, String type, int limit, int offset);
    List<Notification> findUnreadByUserId(Long userId, int limit, int offset);
    int countUnreadByUserId(Long userId);
    int countUnreadByUserIdAndType(Long userId, String type);
    void save(Notification notification);
    void update(Notification notification);
    void markAsRead(Long id);
    void markAllAsRead(Long userId);
    void markAllAsReadByType(Long userId, String type);
    void delete(Long id);
    void deleteByUserId(Long userId);
    void deleteOldNotifications(Long userId, int daysOld);
}