package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.NotificationDao;
import com.chenailin.www.dao.UserDao;
import com.chenailin.www.dao.ArticleDao;
import com.chenailin.www.dao.EnterpriseDao;
import com.chenailin.www.daoimpl.NotificationDaoImpl;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.daoimpl.ArticleDaoImpl;
import com.chenailin.www.daoimpl.EnterpriseDaoImpl;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.pojo.Notification;
import com.chenailin.www.model.pojo.User;
import com.chenailin.www.model.pojo.Article;
import com.chenailin.www.model.pojo.Enterprise;
import com.chenailin.www.model.vo.NotificationVO;
import com.chenailin.www.service.NotificationService;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService
 * @author evi
 */
public class NotificationServiceImpl implements NotificationService {
    private final NotificationDao notificationDao = new NotificationDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final ArticleDao articleDao = new ArticleDaoImpl();
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();

    @Override
    public NotificationVO createNotification(Long userId, String type, String content, Long relatedId) {
        // 验证用户存在
        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 创建通知
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setType(type);
        notification.setContent(content);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        notification.setCreateTime(new Date());

        notificationDao.save(notification);
        return convertToVO(notification);
    }

    @Override
    public void createBatchNotifications(List<Long> userIds, String type, String content, Long relatedId) {
        for (Long userId : userIds) {
            // 验证用户存在
            User user = userDao.findById(userId);
            if (user != null) {
                // 创建通知
                Notification notification = new Notification();
                notification.setUserId(userId);
                notification.setType(type);
                notification.setContent(content);
                notification.setRelatedId(relatedId);
                notification.setIsRead(false);
                notification.setCreateTime(new Date());

                notificationDao.save(notification);
            }
        }
    }

    @Override
    public NotificationVO getNotificationById(Long id, Long userId) {
        // 获取通知
        Notification notification = notificationDao.findById(id);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证通知所属用户
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权访问此通知");
        }

        return convertToVO(notification);
    }

    @Override
    public List<NotificationVO> getAllNotifications(Long userId, int page, int size) {
        // 获取用户的所有通知
        List<Notification> notifications = notificationDao.findByUserId(userId, size, (page - 1) * size);
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public int countAllNotifications(Long userId) {
        System.out.println("还未实现");
        return 0;
//        return notificationDao.countByUserId(userId);
    }

    @Override
    public List<NotificationVO> getNotificationsByType(Long userId, String type, int page, int size) {
        // 获取用户指定类型的通知
        List<Notification> notifications = notificationDao.findByUserIdAndType(userId, type, size, (page - 1) * size);
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public int countNotificationsByType(Long userId, String type) {
        System.out.println("还未实现");
        return 0;
//        return notificationDao.countByUserIdAndType(userId, type);
    }

    @Override
    public List<NotificationVO> getUnreadNotifications(Long userId, int page, int size) {
        // 获取用户未读通知
        List<Notification> notifications = notificationDao.findUnreadByUserId(userId, size, (page - 1) * size);
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public int countUnreadNotifications(Long userId) {
        return notificationDao.countUnreadByUserId(userId);
    }

    @Override
    public void markAsRead(Long notificationId, Long userId) {
        // 获取通知
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证通知所属用户
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }

        // 已读的通知不需要再次标记
        if (notification.isIsRead()) {
            return;
        }

        // 标记为已读
        notification.setIsRead(true);
        notification.setReadTime(new Date());
        notificationDao.update(notification);
    }

    @Override
    public void markAllAsRead(Long userId) {
        notificationDao.markAllAsRead(userId);
    }

    @Override
    public void markAllAsReadByType(Long userId, String type) {
        notificationDao.markAllAsReadByType(userId, type);
    }

    @Override
    public void deleteNotification(Long notificationId, Long userId) {
        // 获取通知
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证通知所属用户
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权删除此通知");
        }

        notificationDao.delete(notificationId);
    }

    @Override
    public void deleteAllNotifications(Long userId) {
        notificationDao.deleteByUserId(userId);
    }

    @Override
    public void deleteReadNotifications(Long userId) {
        System.out.println("还未实现");
        // 删除已读通知
//        notificationDao.deleteReadNotifications(userId);
    }

    @Override
    public List<NotificationVO> getRecentNotifications(Long userId, int limit) {
        // 获取最近的通知
        List<Notification> notifications = notificationDao.findByUserId(userId, limit, 0);
        return notifications.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public void processNotificationAction(Long notificationId, Long userId, String action) {
        // 获取通知
        Notification notification = notificationDao.findById(notificationId);
        if (notification == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证通知所属用户
        if (!notification.getUserId().equals(userId)) {
            throw new BusinessException("无权操作此通知");
        }

        // 根据通知类型和操作执行相应的处理逻辑
        switch (notification.getType()) {
            case Notification.TYPE_ARTICLE_LIKE:
            case Notification.TYPE_ARTICLE_COMMENT:
                // 跳转到文章详情
                // 实际操作由前端执行，这里只需标记已读
                markAsRead(notificationId, userId);
                break;

            case Notification.TYPE_COMMENT_REPLY:
                // 跳转到评论
                markAsRead(notificationId, userId);
                break;

            case Notification.TYPE_PRIVATE_MESSAGE:
                // 跳转到私信
                markAsRead(notificationId, userId);
                break;

            case Notification.TYPE_ENTERPRISE_NOTICE:
                // 跳转到企业通知
                markAsRead(notificationId, userId);
                break;

            case Notification.TYPE_SYSTEM_MESSAGE:
                // 系统消息只需标记已读
                markAsRead(notificationId, userId);
                break;

            default:
                // 未知类型，只标记已读
                markAsRead(notificationId, userId);
        }
    }

    @Override
    public void createArticleNotification(Long articleId, Long authorId, Long actorId, String type, String content) {
        // 验证文章和用户
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        User author = userDao.findById(authorId);
        if (author == null) {
            throw new BusinessException("作者不存在");
        }

        User actor = userDao.findById(actorId);
        if (actor == null) {
            throw new BusinessException("操作者不存在");
        }

        // 不给自己发通知
        if (authorId.equals(actorId)) {
            return;
        }

        // 根据类型生成通知内容
        String notificationContent;
        String notificationType;

        switch (type) {
            case "like":
                notificationType = Notification.TYPE_ARTICLE_LIKE;
                notificationContent = actor.getUsername() + " 点赞了您的文章 《" + article.getTitle() + "》";
                break;

            case "comment":
                notificationType = Notification.TYPE_ARTICLE_COMMENT;
                notificationContent = actor.getUsername() + " 评论了您的文章 《" + article.getTitle() + "》: " + content;
                break;

            default:
                notificationType = Notification.TYPE_ARTICLE_COMMENT;
                notificationContent = actor.getUsername() + " 对您的文章 《" + article.getTitle() + "》 进行了操作";
        }

        createNotification(authorId, notificationType, notificationContent, articleId);
    }

    @Override
    public void createCommentNotification(Long commentId, Long authorId, Long actorId, String type, String content) {
        // 验证用户
        User author = userDao.findById(authorId);
        if (author == null) {
            throw new BusinessException("评论作者不存在");
        }

        User actor = userDao.findById(actorId);
        if (actor == null) {
            throw new BusinessException("操作者不存在");
        }

        // 不给自己发通知
        if (authorId.equals(actorId)) {
            return;
        }

        // 生成通知内容
        String notificationContent = actor.getUsername() + " 回复了您的评论: " + content;
        createNotification(authorId, Notification.TYPE_COMMENT_REPLY, notificationContent, commentId);
    }

    @Override
    public void createEnterpriseNotification(Long enterpriseId, Long userId, String type, String content, Long actorId) {
        // 验证企业和用户
        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        User user = userDao.findById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 生成通知内容
        String notificationContent;

        if (actorId != null) {
            User actor = userDao.findById(actorId);
            String actorName = actor != null ? actor.getUsername() : "管理员";

            switch (type) {
                case "invite":
                    notificationContent = actorName + " 邀请您加入企业 「" + enterprise.getName() + "」";
                    break;

                case "role_change":
                    notificationContent = "您在企业 「" + enterprise.getName() + "」 中的角色已被 " + actorName + " 修改: " + content;
                    break;

                case "department":
                    notificationContent = "您在企业 「" + enterprise.getName() + "」 中的部门发生变更: " + content;
                    break;

                default:
                    notificationContent = "企业 「" + enterprise.getName() + "」 通知: " + content;
            }
        } else {
            notificationContent = "企业 「" + enterprise.getName() + "」 通知: " + content;
        }

        createNotification(userId, Notification.TYPE_ENTERPRISE_NOTICE, notificationContent, enterpriseId);
    }

    @Override
    public void createSystemNotification(String content, Long relatedId) {
        System.out.println("还未实现");

//        // 获取所有用户
//        List<User> users = userDao.findAll();
//
//        // 为每个用户创建系统通知
//        for (User user : users) {
//            createNotification(user.getId(), Notification.TYPE_SYSTEM_MESSAGE, content, relatedId);
//        }
    }

    @Override
    public void deleteOldNotifications(Long userId, int days) {
        notificationDao.deleteOldNotifications(userId, days);
    }

    // 辅助方法：将Notification转换为NotificationVO
    private NotificationVO convertToVO(Notification notification) {
        NotificationVO vo = new NotificationVO();
        vo.setId(notification.getId());
        vo.setUserId(notification.getUserId());
        vo.setType(notification.getType());
        vo.setContent(notification.getContent());
        vo.setRelatedId(notification.getRelatedId());
        vo.setIsRead(notification.isIsRead());
        vo.setReadTime(notification.getReadTime());
        vo.setCreateTime(notification.getCreateTime());

        // 设置类型名称
        switch (notification.getType()) {
            case Notification.TYPE_ARTICLE_LIKE:
                vo.setTypeName("文章点赞");
                break;

            case Notification.TYPE_ARTICLE_COMMENT:
                vo.setTypeName("文章评论");
                break;

            case Notification.TYPE_COMMENT_REPLY:
                vo.setTypeName("评论回复");
                break;

            case Notification.TYPE_PRIVATE_MESSAGE:
                vo.setTypeName("私信");
                break;

            case Notification.TYPE_ENTERPRISE_NOTICE:
                vo.setTypeName("企业通知");
                break;

            case Notification.TYPE_SYSTEM_MESSAGE:
                vo.setTypeName("系统消息");
                break;

            default:
                vo.setTypeName("通知");
        }

        // 根据类型添加相关数据
        Map<String, Object> data = new HashMap<>();

        switch (notification.getType()) {
            case Notification.TYPE_ARTICLE_LIKE:
            case Notification.TYPE_ARTICLE_COMMENT:
                if (notification.getRelatedId() != null) {
                    Article article = articleDao.findById(notification.getRelatedId());
                    if (article != null) {
                        data.put("articleId", article.getId());
                        data.put("articleTitle", article.getTitle());
                    }
                }
                break;

            case Notification.TYPE_ENTERPRISE_NOTICE:
                if (notification.getRelatedId() != null) {
                    Enterprise enterprise = enterpriseDao.findById(notification.getRelatedId());
                    if (enterprise != null) {
                        data.put("enterpriseId", enterprise.getId());
                        data.put("enterpriseName", enterprise.getName());
                    }
                }
                break;
        }

        vo.setData(data);

        return vo;
    }
}