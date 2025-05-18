package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.*;
import com.chenailin.www.daoimpl.*;
import com.chenailin.www.exception.BusinessException;
import com.chenailin.www.model.dto.ArticleDTO;
import com.chenailin.www.model.enums.KnowledgeRole;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.vo.ArticleVO;
import com.chenailin.www.service.ArticleService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author evi
 */
public class ArticleServiceImpl implements ArticleService {
    private final ArticleDao articleDao = new ArticleDaoImpl();
    private final KnowledgeBaseDao knowledgeBaseDao = new KnowledgeBaseDaoImpl();
    private final KnowledgeMemberDao knowledgeMemberDao = new KnowledgeMemberDaoImpl();
    private final CommentDao commentDao = new CommentDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final ArticleLikeDao articleLikeDao = new ArticleLikeDaoImpl();
    private final ArticleFavoriteDao articleFavoriteDao = new ArticleFavoriteDaoImpl();

    @Override
    public Article createArticle(ArticleDTO dto, Long authorId) {
        // 验证知识库存在
        KnowledgeBase knowledgeBase = knowledgeBaseDao.findById(dto.getKnowledgeBaseId());
        if (knowledgeBase == null) {
            throw new BusinessException("知识库不存在");
        }

        // 检查权限：知识库成员才能发表文章
        KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(dto.getKnowledgeBaseId(), authorId);
        if (member == null) {
            throw new BusinessException("您不是该知识库的成员，无法发表文章");
        }

        // 创建文章
        Article article = new Article();
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setKnowledgeId(dto.getKnowledgeBaseId());
        article.setAuthorId(authorId);
        article.setViewCount(0);
        article.setLikeCount(0);
        article.setCommentCount(0);
        article.setPinned(false);
        article.setCreateTime(new Date());
        article.setUpdateTime(new Date());

        articleDao.save(article);

        // 更新知识库文章数量
//        knowledgeBase.setArticleCount(knowledgeBase.getArticleCount() + 1);
//        knowledgeBaseDao.update(knowledgeBase);

        return article;
    }

    @Override
    public Article updateArticle(ArticleDTO dto, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(dto.getId());
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 检查权限：只有文章作者或知识库管理员可以修改文章
        if (!article.getAuthorId().equals(userId)) {
            // 不是作者，检查是否是管理员
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null || member.getRole() > KnowledgeRole.ADMIN.code) {
                throw new BusinessException("您没有权限修改此文章");
            }
        }

        // 更新文章
        article.setTitle(dto.getTitle());
        article.setContent(dto.getContent());
        article.setUpdateTime(new Date());

        articleDao.update(article);
        return article;
    }

    @Override
    public void deleteArticle(Long id, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 检查权限：只有文章作者或知识库管理员可以删除文章
        if (!article.getAuthorId().equals(userId)) {
            // 不是作者，检查是否是管理员
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null || member.getRole() > KnowledgeRole.ADMIN.code) {
                throw new BusinessException("您没有权限删除此文章");
            }
        }

        // 删除文章的评论
        commentDao.deleteByArticleId(id);

        // 删除文章的点赞记录
        articleLikeDao.deleteByArticleId(id);

        // 删除文章
        articleDao.delete(id);

        // 更新知识库文章数量
        KnowledgeBase knowledgeBase = knowledgeBaseDao.findById(article.getKnowledgeId());
        if (knowledgeBase != null) {
//            knowledgeBase.setArticleCount(Math.max(0, knowledgeBase.getArticleCount() - 1));
            knowledgeBaseDao.update(knowledgeBase);
        }
    }

    @Override
    public ArticleVO getArticleById(Long id, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(id);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证知识库存在
        KnowledgeBase knowledgeBase = knowledgeBaseDao.findById(article.getKnowledgeId());
        if (knowledgeBase == null) {
            throw new BusinessException("文章所属知识库不存在");
        }

        // 检查权限：私有知识库的文章只有成员可以查看
        if (!knowledgeBase.getIsPublic()) {
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null) {
                throw new BusinessException("您没有权限查看此文章");
            }
        }

        // 增加浏览量
        articleDao.incrementViewCount(id);

        // 转换为VO对象
        return convertToVO(article, userId);
    }

//    @Override
//    public List<ArticleVO> getKnowledgeArticles(Long knowledgeId, Long userId, int page, int size) {
//        // 验证知识库存在
//        KnowledgeBase knowledgeBase = knowledgeBaseDao.findById(knowledgeId);
//        if (knowledgeBase == null) {
//            throw new BusinessException("知识库不存在");
//        }
//
//        // 检查权限：私有知识库的文章只有成员可以查看
//        if (!knowledgeBase.getIsPublic()) {
//            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(knowledgeId, userId);
//            if (member == null) {
//                throw new BusinessException("您没有权限查看此知识库的文章");
//            }
//        }
//
//        // 获取知识库文章 (添加分页支持)
//        int offset = (page - 1) * size;
//        List<Article> articles = articleDao.findByKnowledgeId(knowledgeId);
//
//        // 转换为VO对象
//        return articles.stream()
//                .map(article -> convertToVO(article, userId))
//                .collect(Collectors.toList());
//    }

    @Override
    public List<ArticleVO> getLatestArticles(int page, int size) {
        int offset = (page - 1) * size;
        List<Article> articles = articleDao.findLatest(size, offset);

        // Only return public knowledge base articles
        List<ArticleVO> result = new ArrayList<>();
        for (Article article : articles) {
            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
            if (kb != null && kb.getIsPublic()) {
                result.add(convertToVO(article, null));
            }
        }

        return result;
    }

    @Override
    public List<ArticleVO> getHottestArticles(int page, int size) {
        int offset = (page - 1) * size;
        List<Article> articles = articleDao.findHottest(size, offset);

        // Only return public knowledge base articles
        List<ArticleVO> result = new ArrayList<>();
        for (Article article : articles) {
            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
            if (kb != null && kb.getIsPublic()) {
                result.add(convertToVO(article, null));
            }
        }

        return result;
    }

    @Override
    public void likeArticle(Long articleId, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证文章可见性（公开知识库或用户是知识库成员）
        KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
        if (!kb.getIsPublic()) {
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null) {
                throw new BusinessException("您没有权限点赞此文章");
            }
        }

        // 检查用户是否已点赞
        ArticleLike existingLike = articleLikeDao.findByArticleAndUser(articleId, userId);
        if (existingLike != null) {
            throw new BusinessException("您已经点赞过此文章");
        }

        // 创建点赞记录
        ArticleLike like = new ArticleLike();
        like.setArticleId(articleId);
        like.setUserId(userId);
        like.setCreateTime(new Date());
        articleLikeDao.save(like);

        // 增加点赞数
        articleDao.incrementLikeCount(articleId);
    }

    @Override
    public void unlikeArticle(Long articleId, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 检查用户是否已点赞
        ArticleLike existingLike = articleLikeDao.findByArticleAndUser(articleId, userId);
        if (existingLike == null) {
            throw new BusinessException("您尚未点赞此文章");
        }

        // 删除点赞记录
        articleLikeDao.delete(existingLike.getId());

        // 减少点赞数
        articleDao.decrementLikeCount(articleId);
    }

    @Override
    public List<ArticleVO> listArticlesByKnowledgeBase(Long knowledgeBaseId, Long userId) {
        return Collections.emptyList();
    }

//    @Override
//    public List<ArticleVO> searchArticles(String keyword, int page, int size) {
//        int offset = (page - 1) * size;
//        List<Article> articles = articleDao.searchByKeyword(keyword, size, offset);
//
//        // 只返回公开知识库的文章
//        List<ArticleVO> result = new ArrayList<>();
//        for (Article article : articles) {
//            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
//            if (kb != null && kb.getIsPublic()) {
//                result.add(convertToVO(article, null));
//            }
//        }
//
//        return result;
//    }

    @Override
    public List<ArticleVO> listArticlesByKnowledgeBase(Long knowledgeBaseId, Long userId, int page, int size) {
        // Verify knowledge base exists
        KnowledgeBase knowledgeBase = knowledgeBaseDao.findById(knowledgeBaseId);
        if (knowledgeBase == null) {
            throw new BusinessException("知识库不存在");
        }

        // Check permissions: only members can view articles in private knowledge bases
        if (!knowledgeBase.getIsPublic()) {
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(knowledgeBaseId, userId);
            if (member == null) {
                throw new BusinessException("您没有权限查看此知识库的文章");
            }
        }

        // Get knowledge base articles (add pagination support)
        int offset = (page - 1) * size;
        List<Article> articles = articleDao.findByKnowledgeIdWithPagination(knowledgeBaseId, size, offset);

        // Convert to VO objects
        return articles.stream()
                .map(article -> convertToVO(article, userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ArticleVO> getRecentArticles(int limit, Long userId) {
        // 获取最近的文章
        List<Article> articles = articleDao.findLatest(limit, 0);

        // 筛选出用户可以访问的文章（公开知识库的文章或用户是知识库成员的文章）
        List<ArticleVO> result = new ArrayList<>();
        for (Article article : articles) {
            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
            if (kb == null) continue;

            if (kb.getIsPublic()) {
                // 公开知识库的文章，任何人可见
                result.add(convertToVO(article, userId));
            } else if (userId != null) {
                // 私有知识库的文章，检查用户是否是知识库成员
                KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(kb.getId(), userId);
                if (member != null) {
                    result.add(convertToVO(article, userId));
                }
            }

            // 如果已经达到限制数量，停止添加
            if (result.size() >= limit) {
                break;
            }
        }

        return result;
    }

    @Override
    public List<ArticleVO> getPopularArticles(int limit) {
        // 获取热门文章（按点赞数排序）
        List<Article> articles = articleDao.findHottest(limit, 0);

        // 只返回公开知识库的文章
        List<ArticleVO> result = new ArrayList<>();
        for (Article article : articles) {
            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
            if (kb != null && kb.getIsPublic()) {
                result.add(convertToVO(article, null));
            }

            // 如果已经达到限制数量，停止添加
            if (result.size() >= limit) {
                break;
            }
        }

        return result;
    }

    @Override
    public void favoriteArticle(Long articleId, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 验证文章可见性（公开知识库或用户是知识库成员）
        KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());
        if (kb == null) {
            throw new BusinessException("文章所属知识库不存在");
        }

        if (!kb.getIsPublic()) {
            KnowledgeMember member = knowledgeMemberDao.findByKnowledgeAndUser(article.getKnowledgeId(), userId);
            if (member == null) {
                throw new BusinessException("您没有权限收藏此文章");
            }
        }

        // 检查用户是否已收藏
        ArticleFavorite existingFav = articleFavoriteDao.findByArticleAndUser(articleId, userId);
        if (existingFav != null) {
            throw new BusinessException("您已经收藏过此文章");
        }

        // 创建收藏记录
        ArticleFavorite favorite = new ArticleFavorite();
        favorite.setArticleId(articleId);
        favorite.setUserId(userId);
        favorite.setCreateTime(new Date());
        articleFavoriteDao.save(favorite);
    }

    @Override
    public void unfavoriteArticle(Long articleId, Long userId) {
        // 验证文章存在
        Article article = articleDao.findById(articleId);
        if (article == null) {
            throw new BusinessException("文章不存在");
        }

        // 检查用户是否已收藏
        ArticleFavorite existingFav = articleFavoriteDao.findByArticleAndUser(articleId, userId);
        if (existingFav == null) {
            throw new BusinessException("您尚未收藏此文章");
        }

        // 删除收藏记录
        articleFavoriteDao.delete(existingFav.getId());
    }

    private ArticleVO convertToVO(Article article, Long currentUserId) {
        ArticleVO vo = new ArticleVO();
        vo.setId(article.getId());
        vo.setTitle(article.getTitle());
        vo.setContent(article.getContent());
        vo.setKnowledgeId(article.getKnowledgeId());
        vo.setAuthorId(article.getAuthorId());

        // 设置作者信息
        User author = userDao.findById(article.getAuthorId());
        if (author != null) {
            vo.setAuthorName(author.getUsername());
            vo.setAuthorAvatar(author.getAvatar());
        }

//        // 设置知识库信息
//        KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeBaseId());
//        if (kb != null) {
//            vo.setKnowledgeBaseName(kb.getName());
//        }

        vo.setViewCount(article.getViewCount());
        vo.setLikeCount(article.getLikeCount());
        vo.setCommentCount(article.getCommentCount());
        vo.setPinned(article.isPinned());
        vo.setCreateTime(article.getCreateTime());
        vo.setUpdateTime(article.getUpdateTime());

        // 设置当前用户是否点赞
        if (currentUserId != null) {
            ArticleLike like = articleLikeDao.findByArticleAndUser(article.getId(), currentUserId);
            vo.setLiked(like != null);
        } else {
            vo.setLiked(false);
        }

        return vo;
    }
}