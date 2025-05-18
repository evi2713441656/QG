package com.chenailin.www.service;

import com.chenailin.www.model.dto.CommentDTO;
import com.chenailin.www.model.pojo.Comment;
import com.chenailin.www.model.vo.CommentVO;

import java.util.List;

/**
 * @author evi
 */
public interface CommentService {
    Comment addComment(CommentDTO dto, Long userId);

    void deleteComment(Long id, Long userId);

    List<CommentVO> getArticleComments(Long articleId);

    List<CommentVO> getUserComments(Long userId, int page, int size);

    List<CommentVO> getCommentsByArticle(Long articleId);

    List<CommentVO> getCommentsByUser(Long targetUserId);

    CommentVO getCommentById(Long commentId);

    Comment updateComment(Long commentId, CommentDTO dto, Long userId);
}
