package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.*;
import com.chenailin.www.daoimpl.*;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.CommentDTO;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.vo.CommentVO;
import com.chenailin.www.service.CommentService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author evi
 */
public class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao = new CommentDaoImpl();
    private final ArticleDao articleDao = new ArticleDaoImpl();
    private final KnowledgeBaseDao knowledgeBaseDao = new KnowledgeBaseDaoImpl();
    private final KnowledgeMemberDao knowledgeMemberDao = new KnowledgeMemberDaoImpl();
    private final UserDao userDao = new UserDaoImpl();

    @Override
    public Comment addComment(CommentDTO dto, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(dto.getArticleId());
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证文章可见性（公开知识库或用户是知识库成员）
        KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
        if (!kb.getIsPublic()) {
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null) {
                throw new BusinessException("您没有权限评论此文章");
            }
        }

        // 如果是回复评论，验证父评论存在
        if (dto.getParentId() != null) {
            Comment parentComment = commentDao.findById(dto.getParentId());
            if (parentComment == null) {
                throw new BusinessException("回复的评论不存在");
            }

            // 确保父评论属于同一篇文章
            if (!parentComment.getArticleId().equals(dto.getArticleId())) {
                throw new BusinessException("回复的评论不属于此文章");
            }
        }

        // 创建评论
        Comment comment = new Comment();
        comment.setContent(dto.getContent());
        comment.setArticleId(dto.getArticleId());
        comment.setUserId(userId);
        comment.setParentId(dto.getParentId());
        comment.setCreateTime(new Date());

        commentDao.save(comment);

        // 增加文章评论数
        articleDao.incrementCommentCount(dto.getArticleId());

        return comment;
    }

    @Override
    public void deleteComment(Long id, Long userId) {
        // 验证评论存在
        Comment comment = commentDao.findById(id);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 只有评论者或文章作者可以删除评论
        if (!comment.getUserId().equals(userId)) {
            // 不是评论者，检查是否是文章作者
            Article article = articleDao.findById(comment.getArticleId());
            if (article == null || !article.getAuthorId().equals(userId)) {
                throw new BusinessException("您没有权限删除此评论");
            }
        }

        // 删除评论
        commentDao.delete(id);
    }

    @Override
    public List<CommentVO> getArticleComments(Long articleId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 获取所有评论
        List<Comment> allComments = commentDao.findByArticleId(articleId);

        // 构建评论树
        return buildCommentTree(allComments);
    }

    @Override
    public List<CommentVO> getUserComments(Long userId, int page, int size) {
        // TODO: 实现分页获取用户评论，暂时返回所有评论
        List<Comment> comments = commentDao.findByUserId(userId);

        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentVO> getCommentsByArticle(Long articleId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 获取所有评论
        List<Comment> allComments = commentDao.findByArticleId(articleId);

        // 构建评论树
        return buildCommentTree(allComments);
    }

    @Override
    public List<CommentVO> getCommentsByUser(Long targetUserId) {
        // 验证用户存在
        User user = userDao.findById(targetUserId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 获取用户的所有评论
        List<Comment> comments = commentDao.findByUserId(targetUserId);

        // 转换为VO列表
        return comments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public CommentVO getCommentById(Long commentId) {
        // 验证评论存在
        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 转换为VO
        return convertToVO(comment);
    }

    @Override
    public Comment updateComment(Long commentId, CommentDTO dto, Long userId) {
        // 验证评论存在
        Comment comment = commentDao.findById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 验证内容
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) {
            throw new BusinessException("评论内容不能为空");
        }

        // 检查权限：只有评论作者可以修改评论
        if (!comment.getUserId().equals(userId)) {
            throw new BusinessException("您没有权限修改此评论");
        }

        // 更新评论
        comment.setContent(dto.getContent());
//        comment.setUpdateTime(new Date());
//        commentDao.update(comment);

        return comment;
    }

    // 辅助方法：构建评论树
    private List<CommentVO> buildCommentTree(List<Comment> allComments) {
        // 将所有评论转换为VO
        List<CommentVO> commentVOs = allComments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 建立ID到VO的映射
        Map<Long, CommentVO> idToVO = commentVOs.stream()
                .collect(Collectors.toMap(CommentVO::getId, vo -> vo));

        // 构建评论树
        List<CommentVO> rootComments = new ArrayList<>();
        for (CommentVO vo : commentVOs) {
            if (vo.getParentId() == null) {
                // 根评论
                rootComments.add(vo);
            } else {
                // 子评论，添加到父评论的回复列表
                CommentVO parentVO = idToVO.get(vo.getParentId());
                if (parentVO != null) {
                    if (parentVO.getReplies() == null) {
                        parentVO.setReplies(new ArrayList<>());
                    }
                    parentVO.getReplies().add(vo);
                }
            }
        }

        return rootComments;
    }

    // 辅助方法：将Comment转换为CommentVO
    private CommentVO convertToVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setContent(comment.getContent());
        vo.setArticleId(comment.getArticleId());
        vo.setUserId(comment.getUserId());
        vo.setParentId(comment.getParentId());
        vo.setCreateTime(comment.getCreateTime());
//        vo.setUpdateTime(comment.getUpdateTime());

        // 设置用户信息
        User user = userDao.findById(comment.getUserId());
        if (user != null) {
            vo.setUserName(user.getUsername());
            vo.setUserAvatar(user.getAvatar());
        }

//        // 设置文章信息
//        Article article = articleDao.findById(comment.getArticleId());
//        if (article != null) {
//            vo.setArticleTitle(article.getTitle());
//        }

        return vo;
    }

}