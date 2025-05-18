package com.chenailin.www.service;

import com.chenailin.www.model.vo.BrowseHistoryVO;

import java.util.List;

/**
 * @author evi
 */
public interface BrowseHistoryService {
    void recordBrowseHistory(Long userId, Long articleId);

    List<BrowseHistoryVO> getUserBrowseHistory(Long userId, int page, int size);

    void deleteBrowseHistory(Long id, Long userId);

    void clearBrowseHistory(Long userId);

    void clearUserBrowseHistory(Long userId);
}
