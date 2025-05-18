package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.ArticleLike;

import java.util.List;

/**
 * 文章点赞数据访问接口
 * @author evi
 */
public interface ArticleLikeDao {

    Long save(ArticleLike articleLike);
    void delete(Long id);
    ArticleLike findByArticleAndUser(Long articleId, Long userId);
    List<ArticleLike> findByArticleId(Long articleId);
    List<ArticleLike> findByUserId(Long userId);
    int deleteByArticleId(Long articleId);
    int deleteByUserId(Long userId);
    int countByArticleId(Long articleId);
}