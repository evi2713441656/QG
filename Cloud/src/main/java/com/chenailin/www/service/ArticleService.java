package com.chenailin.www.service;

import com.chenailin.www.model.dto.ArticleDTO;
import com.chenailin.www.model.pojo.Article;
import com.chenailin.www.model.vo.ArticleVO;

import java.util.List;

/**
 * @author evi
 */
public interface ArticleService {
    Article createArticle(ArticleDTO dto, Long authorId);

    Article updateArticle(ArticleDTO dto, Long userId);

    void deleteArticle(Long id, Long userId);

    ArticleVO getArticleById(Long id, Long userId);

//    List<ArticleVO> getKnowledgeArticles(Long knowledgeId, Long userId, int page, int size);

    List<ArticleVO> getLatestArticles(int page, int size);

    List<ArticleVO> getHottestArticles(int page, int size);

    void likeArticle(Long articleId, Long userId);

    void unlikeArticle(Long articleId, Long userId);

//    List<ArticleVO> searchArticles(String keyword, int page, int size);

    List<ArticleVO> listArticlesByKnowledgeBase(Long knowledgeBaseId, Long userId);

    List<ArticleVO> listArticlesByKnowledgeBase(Long knowledgeBaseId, Long userId, int page, int size);

    List<ArticleVO> getRecentArticles(int limit, Long userId);

    List<ArticleVO> getPopularArticles(int limit);

    void favoriteArticle(Long articleId, Long userId);

    void unfavoriteArticle(Long articleId, Long userId);
}
