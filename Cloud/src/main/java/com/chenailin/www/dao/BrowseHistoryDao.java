package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.BrowseHistory;

import java.util.List;

/**
 * @author evi
 */
public interface BrowseHistoryDao {
    List<BrowseHistory> findByUserId(Long userId, int limit, int offset);
    void save(BrowseHistory browseHistory);
    void delete(Long id);
    int deleteByUserId(Long userId);
}