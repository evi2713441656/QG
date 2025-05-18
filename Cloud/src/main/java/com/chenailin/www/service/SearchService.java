package com.chenailin.www.service;

import com.chenailin.www.model.vo.ArticleVO;
import com.chenailin.www.model.vo.KnowledgeBaseVO;
import com.chenailin.www.model.vo.SearchResultVO;
import com.chenailin.www.model.vo.UserVO;

/**
 * @author evi
 */
public interface SearchService {
    SearchResultVO search(String keyword);

    ArticleVO searchArticle(String keyword);

    KnowledgeBaseVO searchKnowledgeBase(String keyword);

    UserVO searchUser(String keyword);

//    SearchResultVO globalSearch(String keyword);
}
