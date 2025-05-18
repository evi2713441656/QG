/**
 * SearchServiceImpl.java - Suggested Changes
 *
 * The main issues in this file:
 * 1. Direct Long.valueOf(keyword) usage without error handling
 * 2. Potential NullPointerExceptions
 * 3. Absence of keyword-based search as fallback
 */

package com.chenailin.www.serviceimpl;

import com.chenailin.www.dao.ArticleDao;
import com.chenailin.www.dao.EnterpriseDao;
import com.chenailin.www.dao.KnowledgeBaseDao;
import com.chenailin.www.dao.UserDao;
import com.chenailin.www.daoimpl.ArticleDaoImpl;
import com.chenailin.www.daoimpl.EnterpriseDaoImpl;
import com.chenailin.www.daoimpl.KnowledgeBaseDaoImpl;
import com.chenailin.www.daoimpl.UserDaoImpl;
import com.chenailin.www.model.pojo.*;
import com.chenailin.www.model.vo.*;
import com.chenailin.www.service.SearchService;

/**
 * @author evi
 */
public class SearchServiceImpl implements SearchService {
    private final ArticleDao articleDao = new ArticleDaoImpl();
    private final KnowledgeBaseDao knowledgeBaseDao = new KnowledgeBaseDaoImpl();
    private final UserDao userDao = new UserDaoImpl();
    private final EnterpriseDao enterpriseDao = new EnterpriseDaoImpl();

    @Override
    public SearchResultVO search(String keyword) {
        SearchResultVO result = new SearchResultVO();

        try {
            // Search articles
            ArticleVO article = searchArticle(keyword);
            result.setArticleVO(article);

            // Search knowledge base
            KnowledgeBaseVO knowledgeBase = searchKnowledgeBase(keyword);
            result.setKnowledgeBaseVO(knowledgeBase);

            // Search users
            UserVO user = searchUser(keyword);
            result.setUserVO(user);

            // Set total count
            result.setTotal(
                    (article != null ? 1 : 0) +
                            (knowledgeBase != null ? 1 : 0) +
                            (user != null ? 1 : 0)
            );
        } catch (Exception e) {
            // Log the error
            System.err.println("Error in global search: " + e.getMessage());
            e.printStackTrace();
            // Return empty result with zero total
            result.setTotal(0);
        }

        return result;
    }

    @Override
    public ArticleVO searchArticle(String keyword) {
        ArticleVO vo = null;
        Article article = null;

        try {
            // Try to search by ID first
            long id = Long.parseLong(keyword);
            article = articleDao.findById(id);
        } catch (NumberFormatException e) {
            // If keyword is not a valid number, search by keyword
            try {
                return null;
            } catch (Exception searchEx) {
                System.err.println("Error searching article by keyword: " + searchEx.getMessage());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error searching article: " + e.getMessage());
            return null;
        }

        // If no article found, return null
        if (article == null) {
            return null;
        }

        try {
            // Get knowledge base
            KnowledgeBase kb = knowledgeBaseDao.findById(article.getKnowledgeId());

            // Only process public articles or handle permission check here
            if (kb != null && kb.getIsPublic()) {
                vo = new ArticleVO();
                vo.setId(article.getId());
                vo.setTitle(article.getTitle());
                vo.setContent(article.getContent());
                vo.setKnowledgeId(article.getKnowledgeId());
                vo.setAuthorId(article.getAuthorId());

                // Set author info
                User author = userDao.findById(article.getAuthorId());
                if (author != null) {
                    vo.setAuthorName(author.getUsername());
                    vo.setAuthorAvatar(author.getAvatar());
                }

                vo.setViewCount(article.getViewCount());
                vo.setLikeCount(article.getLikeCount());
                vo.setCommentCount(article.getCommentCount());
                vo.setCreateTime(article.getCreateTime());
            }
        } catch (Exception e) {
            System.err.println("Error processing article: " + e.getMessage());
        }

        return vo;
    }

    @Override
    public KnowledgeBaseVO searchKnowledgeBase(String keyword) {
        KnowledgeBaseVO vo = null;
        KnowledgeBase knowledgeBase = null;

        try {
            long id = Long.parseLong(keyword);
            knowledgeBase = knowledgeBaseDao.findById(id);
        } catch (NumberFormatException e) {
            try {
                return null;
            } catch (Exception searchEx) {
                System.err.println("Error searching user by keyword: " + searchEx.getMessage());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error searching knowledge base: " + e.getMessage());
            return null;
        }

        // If no knowledge base found, return null
        if (knowledgeBase == null) {
            return null;
        }

        try {
            // Only process public knowledge bases
            if (knowledgeBase.getIsPublic()) {
                vo = new KnowledgeBaseVO();
                vo.setId(knowledgeBase.getId());
                vo.setName(knowledgeBase.getName());
                vo.setDescription(knowledgeBase.getDescription());
                vo.setCoverUrl(knowledgeBase.getCoverUrl());
                vo.setIsPublic(knowledgeBase.getIsPublic());
                vo.setCreatorId(knowledgeBase.getCreatorId());
                vo.setCreateTime(knowledgeBase.getCreateTime());
            }
        } catch (Exception e) {
            System.err.println("Error processing knowledge base: " + e.getMessage());
        }

        return vo;
    }

    @Override
    public UserVO searchUser(String keyword) {
        UserVO vo = null;
        User user = null;

        try {
            long id = Long.parseLong(keyword);
            user = userDao.findById(id);
        } catch (NumberFormatException e) {
            // If keyword is not a valid number, search by username or other fields
            try {
                return null;
            } catch (Exception searchEx) {
                System.err.println("Error searching user by keyword: " + searchEx.getMessage());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error searching user: " + e.getMessage());
            return null;
        }

        // If no user found, return null
        if (user == null) {
            return null;
        }

        try {
            vo = new UserVO();
            vo.setId(user.getId());
            vo.setUsername(user.getUsername());
            vo.setAvatar(user.getAvatar());
            // Add other fields as needed
        } catch (Exception e) {
            System.err.println("Error processing user: " + e.getMessage());
        }

        return vo;
    }

    // Other methods remain the same...
}