package com.chenailin.www.service;

import com.chenailin.www.model.dto.EnterpriseNoticeDTO;
import com.chenailin.www.model.vo.EnterpriseNoticeVO;

import java.util.List;

/**
 * Enterprise Notice Service Interface
 * @author evi
 */
public interface EnterpriseNoticeService {
    EnterpriseNoticeVO createNotice(EnterpriseNoticeDTO dto, Long userId);
    EnterpriseNoticeVO updateNotice(EnterpriseNoticeDTO dto, Long userId);
    void deleteNotice(Long id, Long userId);
    EnterpriseNoticeVO getNoticeById(Long id, Long userId);
    List<EnterpriseNoticeVO> listNotices(Long enterpriseId, Long userId, int page, int size);
    int countNotices(Long enterpriseId, Long userId);
    List<EnterpriseNoticeVO> listUserNotices(Long userId, int page, int size);
    List<EnterpriseNoticeVO> listUnreadNotices(Long userId, int page, int size);
    void markAsRead(Long noticeId, Long userId);
    void markAllAsRead(Long userId);
}