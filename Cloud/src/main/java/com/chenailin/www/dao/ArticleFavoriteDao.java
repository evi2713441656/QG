package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.ArticleFavorite;

import java.util.List;

/**
 * @author evi
 */
public interface ArticleFavoriteDao {
    Long save(ArticleFavorite articleFavorite);
    void delete(Long id);
    ArticleFavorite findByArticleAndUser(Long articleId, Long userId);
    List<ArticleFavorite> findByArticleId(Long articleId);
    List<ArticleFavorite> findByUserId(Long userId);
    int deleteByArticleId(Long articleId);
    int deleteByUserId(Long userId);
    int countByArticleId(Long articleId);
}
