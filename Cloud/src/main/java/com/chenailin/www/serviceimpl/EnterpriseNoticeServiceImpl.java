package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.*;
import com.chenailin.www.daoimpl.*;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.EnterpriseNoticeDTO;
import com.chenailin.www.model.enums.EnterpriseRole;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.vo.EnterpriseMemberVO;
import com.chenailin.www.model.vo.EnterpriseNoticeVO;
import com.chenailin.www.service.EnterpriseNoticeService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of EnterpriseNoticeService
 * @author evi
 */
public class EnterpriseNoticeServiceImpl implements EnterpriseNoticeService {
    private final EnterpriseNoticeDao noticeDao = new EnterpriseNoticeDaoImpl();
    private final EnterpriseNoticeRecipientDao recipientDao = new EnterpriseNoticeRecipientDaoImpl();
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();
    private final EnterpriseMemberDao memberDao = new EnterpriseMemberDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public EnterpriseNoticeVO createNotice(EnterpriseNoticeDTO dto, Long userId) {
        // 验证企业存在
        Enterprise enterprise = enterpriseDao.findById(dto.getEnterpriseId());
        if (enterprise == null) {
            throw new BusinessException("企业不存在");
        }

        // 验证用户是企业成员且有权限发布通知
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(dto.getEnterpriseId(), userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

        // 只有所有者、管理员和部门主管可以发布通知
        if (member.getRole() > EnterpriseRole.DEPARTMENT_MANAGER.code) {
            throw new BusinessException("您没有权限发布企业通知");
        }

        // 如果是私有通知，验证有指定接收者
        if (dto.isPrivate() && (dto.getRecipientIds() == null || dto.getRecipientIds().isEmpty())) {
            throw new BusinessException("私有通知必须指定接收者");
        }

        // 创建通知
        EnterpriseNotice notice = new EnterpriseNotice();
        notice.setEnterpriseId(dto.getEnterpriseId());
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPublisherId(userId);
        notice.setIsPrivate(dto.isPrivate());
        notice.setCreateTime(new Date());
        notice.setUpdateTime(new Date());

        noticeDao.save(notice);

        // 如果是私有通知，添加接收者
        if (dto.isPrivate() && dto.getRecipientIds() != null && !dto.getRecipientIds().isEmpty()) {
            List<EnterpriseNoticeRecipient> recipients = new ArrayList<>();

            for (Long recipientId : dto.getRecipientIds()) {
                // 验证接收者是企业成员
                EnterpriseMember recipientMember = memberDao.findByEnterpriseAndUser(dto.getEnterpriseId(), recipientId);
                if (recipientMember != null) {
                    EnterpriseNoticeRecipient recipient = new EnterpriseNoticeRecipient();
                    recipient.setNoticeId(notice.getId());
                    recipient.setUserId(recipientId);
                    recipient.setIsRead(false);
                    recipients.add(recipient);
                }
            }

            if (!recipients.isEmpty()) {
                System.out.println("还未实现");
//                recipientDao.batchSave(recipients);
            }
        }

        return getNoticeById(notice.getId(), userId);
    }

    @Override
    public EnterpriseNoticeVO updateNotice(EnterpriseNoticeDTO dto, Long userId) {
        // 验证通知存在
        EnterpriseNotice notice = noticeDao.findById(dto.getId());
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证用户是发布者或管理员
        if (!notice.getPublisherId().equals(userId)) {
            EnterpriseMember member = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), userId);
            if (member == null || member.getRole() > EnterpriseRole.ADMIN.code) {
                throw new BusinessException("您没有权限修改此通知");
            }
        }

        // 如果更改了私有状态，需要处理接收者
        boolean privacyChanged = notice.isIsPrivate() != dto.isPrivate();

        // 更新通知
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setIsPrivate(dto.isPrivate());
        notice.setUpdateTime(new Date());

        noticeDao.update(notice);

        // 处理接收者变更
        if (privacyChanged) {
            // 如果从公开变为私有，且指定了接收者
            if (dto.isPrivate() && dto.getRecipientIds() != null && !dto.getRecipientIds().isEmpty()) {
                // 删除旧接收者
                recipientDao.deleteByNoticeId(notice.getId());

                // 添加新接收者
                List<EnterpriseNoticeRecipient> recipients = new ArrayList<>();

                for (Long recipientId : dto.getRecipientIds()) {
                    // 验证接收者是企业成员
                    EnterpriseMember recipientMember = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), recipientId);
                    if (recipientMember != null) {
                        EnterpriseNoticeRecipient recipient = new EnterpriseNoticeRecipient();
                        recipient.setNoticeId(notice.getId());
                        recipient.setUserId(recipientId);
                        recipient.setIsRead(false);
                        recipients.add(recipient);
                    }
                }

                if (!recipients.isEmpty()) {
                    System.out.println("还未实现");
//                    recipientDao.batchSave(recipients);
                }
            }
            // 如果从私有变为公开，删除所有接收者记录
            else if (!dto.isPrivate()) {
                recipientDao.deleteByNoticeId(notice.getId());
            }
        }
        // 如果仍然是私有通知，但接收者列表有变更
        else if (dto.isPrivate() && dto.getRecipientIds() != null) {
            // 获取现有接收者
            List<EnterpriseNoticeRecipient> existingRecipients = recipientDao.findByNoticeId(notice.getId());
            List<Long> existingRecipientIds = existingRecipients.stream()
                    .map(EnterpriseNoticeRecipient::getUserId)
                    .collect(Collectors.toList());

            // 找出需要添加的接收者
            List<Long> newRecipientIds = dto.getRecipientIds().stream()
                    .filter(id -> !existingRecipientIds.contains(id))
                    .collect(Collectors.toList());

            // 找出需要删除的接收者
            List<Long> removedRecipientIds = existingRecipientIds.stream()
                    .filter(id -> !dto.getRecipientIds().contains(id))
                    .collect(Collectors.toList());

            // 添加新接收者
            if (!newRecipientIds.isEmpty()) {
                List<EnterpriseNoticeRecipient> newRecipients = new ArrayList<>();

                for (Long recipientId : newRecipientIds) {
                    // 验证接收者是企业成员
                    EnterpriseMember recipientMember = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), recipientId);
                    if (recipientMember != null) {
                        EnterpriseNoticeRecipient recipient = new EnterpriseNoticeRecipient();
                        recipient.setNoticeId(notice.getId());
                        recipient.setUserId(recipientId);
                        recipient.setIsRead(false);
                        newRecipients.add(recipient);
                    }
                }

                if (!newRecipients.isEmpty()) {
                    System.out.println("还未实现");
//                    recipientDao.batchSave(newRecipients);
                }
            }

            // 删除不再需要的接收者
            for (Long recipientId : removedRecipientIds) {
                for (EnterpriseNoticeRecipient recipient : existingRecipients) {
                    if (recipient.getUserId().equals(recipientId)) {
                        recipientDao.delete(recipient.getId());
                        break;
                    }
                }
            }
        }

        return getNoticeById(notice.getId(), userId);
    }

    @Override
    public void deleteNotice(Long id, Long userId) {
        // 验证通知存在
        EnterpriseNotice notice = noticeDao.findById(id);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证用户是发布者或管理员
        if (!notice.getPublisherId().equals(userId)) {
            EnterpriseMember member = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), userId);
            if (member == null || member.getRole() > EnterpriseRole.ADMIN.code) {
                throw new BusinessException("您没有权限删除此通知");
            }
        }

        // 删除所有接收者记录
        recipientDao.deleteByNoticeId(id);

        // 删除通知
        noticeDao.delete(id);
    }

    @Override
    public EnterpriseNoticeVO getNoticeById(Long id, Long userId) {
        // 验证通知存在
        EnterpriseNotice notice = noticeDao.findById(id);
        if (notice == null) {
            throw new BusinessException("通知不存在");
        }

        // 验证用户是企业成员
        EnterpriseMember member = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), userId);
        if (member == null) {
            throw new BusinessException("您不是该企业的成员");
        }

//        // 如果是私有通知，验证用户是接收者
//        if (notice.isIsPrivate()) {
//            System.out.println("还未实现");
//            EnterpriseNoticeRecipient recipient = recipientDao.findByNoticeIdAndUserId(id, userId);
//            if (recipient == null && !notice.getPublisherId().equals(userId) && member.getRole() > EnterpriseRole.ADMIN.code) {
//                throw new BusinessException("您没有权限查看此通知");
//            }
//
//            // 如果用户是接收者且未读，标记为已读
//            if (recipient != null && !recipient.isIsRead()) {
//                markAsRead(id, userId);
//                recipient.setIsRead(true);
//                recipient.setReadTime(new Date());
//            }
//        }

        return convertToVO(notice, userId);
    }

    @Override
    public List<EnterpriseNoticeVO> listNotices(Long enterpriseId, Long userId, int page, int size) {
//        // 验证企业存在
//        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
//        if (enterprise == null) {
//            throw new BusinessException("企业不存在");
//        }
//
//        // 验证用户是企业成员
//        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
//        if (member == null) {
//            throw new BusinessException("您不是该企业的成员");
//        }
//
//        // 获取通知列表
//        List<EnterpriseNotice> notices = noticeDao.findByEnterpriseId(enterpriseId, size, (page - 1) * size);
//
//        // 筛选用户可见的通知
//        List<EnterpriseNotice> visibleNotices = new ArrayList<>();
//
//        for (EnterpriseNotice notice : notices) {
//            // 如果是公开通知，所有成员可见
//            if (!notice.isIsPrivate()) {
//                visibleNotices.add(notice);
//            }
//            // 如果是私有通知，只有发布者、管理员和接收者可见
//            else if (notice.getPublisherId().equals(userId) ||
//                    member.getRole() <= EnterpriseRole.ADMIN.code ||
//                    recipientDao.exists(notice.getId(), userId)) {
//                visibleNotices.add(notice);
//            }
//        }
//
//        // 转换为VO
//        return visibleNotices.stream()
//                .map(notice -> convertToVO(notice, userId))
//                .collect(Collectors.toList());
        System.out.println("还未实现");
        return null;
    }

    @Override
    public int countNotices(Long enterpriseId, Long userId) {
        System.out.println("还未实现");
        return 0;
//        // 验证企业存在
//        Enterprise enterprise = enterpriseDao.findById(enterpriseId);
//        if (enterprise == null) {
//            throw new BusinessException("企业不存在");
//        }
//
//        // 验证用户是企业成员
//        EnterpriseMember member = memberDao.findByEnterpriseAndUser(enterpriseId, userId);
//        if (member == null) {
//            throw new BusinessException("您不是该企业的成员");
//        }
//
//        // 获取公开通知数量
//        int publicCount = noticeDao.countPublicNotices(enterpriseId);
//
//        // 获取私有通知数量（只有发布者、管理员和接收者可见）
//        int privateCount = 0;
//        if (member.getRole() <= EnterpriseRole.ADMIN.code) {
//            // 管理员可见所有私有通知
//            privateCount = noticeDao.countPrivateNotices(enterpriseId);
//        } else {
//            // 普通成员只能看到发给自己的私有通知
//            privateCount = noticeDao.countUserPrivateNotices(enterpriseId, userId);
//        }
//
//        return publicCount + privateCount;
    }

    @Override
    public List<EnterpriseNoticeVO> listUserNotices(Long userId, int page, int size) {
        System.out.println("还未实现");
        return null;
//        // 获取用户可见的所有通知
//        List<EnterpriseNotice> notices = noticeDao.findVisibleByUserId(userId, size, (page - 1) * size);
//
//        // 转换为VO
//        return notices.stream()
//                .map(notice -> convertToVO(notice, userId))
//                .collect(Collectors.toList());
    }

    @Override
    public List<EnterpriseNoticeVO> listUnreadNotices(Long userId, int page, int size) {
        System.out.println("还未实现");
        return null;
//        // 获取用户未读的所有通知
//        List<EnterpriseNotice> notices = noticeDao.findUnreadByUserId(userId, size, (page - 1) * size);
//
//        // 转换为VO
//        return notices.stream()
//                .map(notice -> convertToVO(notice, userId))
//                .collect(Collectors.toList());
    }

    @Override
    public void markAsRead(Long noticeId, Long userId) {
//        // 验证通知存在
//        EnterpriseNotice notice = noticeDao.findById(noticeId);
//        if (notice == null) {
//            throw new BusinessException("通知不存在");
//        }
//
//        // 公开通知不需要标记已读
//        if (!notice.isIsPrivate()) {
//            return;
//        }
//
//        // 查找接收者记录
//        EnterpriseNoticeRecipient recipient = recipientDao.findByNoticeIdAndUserId(noticeId, userId);
//        if (recipient == null) {
//            // 如果不是接收者，检查是否是企业成员
//            EnterpriseMember member = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), userId);
//            if (member == null) {
//                throw new BusinessException("您不是该企业的成员");
//            }
//
//            // 创建接收者记录
//            recipient = new EnterpriseNoticeRecipient();
//            recipient.setNoticeId(noticeId);
//            recipient.setUserId(userId);
//            recipient.setIsRead(true);
//            recipient.setReadTime(new Date());
//            recipientDao.save(recipient);
//        } else if (!recipient.isIsRead()) {
//            // 更新已读状态
//            recipient.setIsRead(true);
//            recipient.setReadTime(new Date());
//            recipientDao.update(recipient);
//        }
        System.out.println("还未实现");
    }

    @Override
    public void markAllAsRead(Long userId) {
        // 标记所有私有通知为已读
        recipientDao.markAllAsRead(userId);
    }

    // 辅助方法：将EnterpriseNotice转换为VO
    private EnterpriseNoticeVO convertToVO(EnterpriseNotice notice, Long userId) {
        EnterpriseNoticeVO vo = new EnterpriseNoticeVO();
        vo.setId(notice.getId());
        vo.setEnterpriseId(notice.getEnterpriseId());
        vo.setTitle(notice.getTitle());
        vo.setContent(notice.getContent());
        vo.setPublisherId(notice.getPublisherId());
        vo.setPrivate(notice.isIsPrivate());
        vo.setCreateTime(notice.getCreateTime());
        vo.setUpdateTime(notice.getUpdateTime());

        // 获取企业信息
        Enterprise enterprise = enterpriseDao.findById(notice.getEnterpriseId());
        if (enterprise != null) {
            vo.setEnterpriseName(enterprise.getName());
        }

        // 获取发布者信息
        User publisher = userDao.findById(notice.getPublisherId());
        if (publisher != null) {
            vo.setPublisherName(publisher.getUsername());
            vo.setPublisherAvatar(publisher.getAvatar());
        }

        // 如果是私有通知，获取已读状态
        if (notice.isIsPrivate()) {
//            EnterpriseNoticeRecipient recipient = recipientDao.findByNoticeIdAndUserId(notice.getId(), userId);
//            vo.setRead(recipient != null && recipient.isIsRead());
//            vo.setReadTime(recipient != null ? recipient.getReadTime() : null);
//
//            // 获取已读人数和总接收者数（管理员和发布者可见）
//            EnterpriseMember member = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), userId);
            System.out.println("还未实现");

//            if (member != null && (member.getRole() <= EnterpriseRole.ADMIN.code || notice.getPublisherId().equals(userId))) {
//                vo.setReadCount(recipientDao.countReadByNoticeId(notice.getId()));
//                vo.setTotalRecipients(recipientDao.countByNoticeId(notice.getId()));
//
//                // 是否获取接收者列表（可选，可能会影响性能）
//                // 这里可以根据需要从参数传入
//                boolean includeRecipients = false;
//                if (includeRecipients) {
//                    List<EnterpriseNoticeRecipient> recipients = recipientDao.findByNoticeId(notice.getId());
//                    List<EnterpriseMemberVO> recipientVOs = new ArrayList<>();
//
//                    for (EnterpriseNoticeRecipient r : recipients) {
//                        EnterpriseMember recipientMember = memberDao.findByEnterpriseAndUser(notice.getEnterpriseId(), r.getUserId());
//                        if (recipientMember != null) {
//                            EnterpriseMemberVO memberVO = new EnterpriseMemberVO();
//                            memberVO.setId(recipientMember.getId());
//                            memberVO.setUserId(recipientMember.getUserId());
//
//                            // 获取用户信息
//                            User user = userDao.findById(recipientMember.getUserId());
//                            if (user != null) {
//                                memberVO.setUsername(user.getUsername());
//                                memberVO.setAvatar(user.getAvatar());
//                            }
//
//                            // 设置已读状态
//                            memberVO.setRead(r.isIsRead());
//                            memberVO.setReadTime(r.getReadTime());
//
//                            recipientVOs.add(memberVO);
//                        }
//                    }
//
//                    vo.setRecipients(recipientVOs);
//                }
//            }
        } else {
            // 公开通知没有已读状态
            vo.setRead(true);
        }

        return vo;
    }
}