package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.Comment;

import java.util.List;

/**
 * @author evi
 */
public interface CommentDao {
    Comment findById(Long id);
    List<Comment> findByArticleId(Long articleId);
    List<Comment> findByUserId(Long userId);
    List<Comment> findReplies(Long parentId);
    void save(Comment comment);
    void delete(Long id);
    void deleteByArticleId(Long articleId);
    int countByArticleId(Long articleId);
}