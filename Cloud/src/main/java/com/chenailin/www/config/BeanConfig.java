package com.chenailin.www.config;

import com.chenailin.www.serviceimpl.*;
import com.chenailin.www.core.container.BeanContainer;

/**
 * @author evi
 */
public class BeanConfig {
    public static void configureBeans() {
        BeanContainer container = BeanContainer.getInstance();
        // 注册服务Bean
        container.registerBean("articleService", new ArticleServiceImpl());
        container.registerBean("authService", new AuthServiceImpl());
        container.registerBean("browseHistoryService", new BrowseHistoryServiceImpl());
        container.registerBean("commentService", new CommentServiceImpl());
        container.registerBean("enterpriseService", new EnterpriseServiceImpl());
        container.registerBean("knowledgeService", new KnowledgeServiceImpl());
        container.registerBean("searchService", new SearchServiceImpl());
        container.registerBean("userService", new UserServiceImpl());
        container.registerBean("verificationCodeService", new VerificationCodeServiceImpl());
    }
}