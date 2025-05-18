package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.Article;

import java.util.List;

/**
 * @author evi
 */
public interface ArticleDao {
    Article findById(Long id);

    List<Article> findByKnowledgeIdWithPagination(Long knowledgeId, int limit, int offset);

    List<Article> findByKnowledgeId(Long knowledgeId);
    List<Article> findLatest(int limit, int offset);
    List<Article> findHottest(int limit, int offset);
    void save(Article article);
    void update(Article article);
    void delete(Long id);
    void incrementViewCount(Long id);
    void incrementLikeCount(Long id);
    void decrementLikeCount(Long id);
    void incrementCommentCount(Long id);
//    List<Article> searchByKeyword(String keyword, int limit, int offset);
}
