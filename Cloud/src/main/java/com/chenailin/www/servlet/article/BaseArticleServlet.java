package com.chenailin.www.servlet.article;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.core.servlet.BaseServlet;
import com.chenailin.www.service.ArticleService;
import com.chenailin.www.service.AuthService;
import com.chenailin.www.service.CommentService;
import com.chenailin.www.service.VerificationCodeService;
import com.chenailin.www.serviceimpl.ArticleServiceImpl;
import com.chenailin.www.serviceimpl.CommentServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * 文章相关Servlet的基类
 * @author evi
 */
public abstract class BaseArticleServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseArticleServlet.class);

    protected ArticleService articleService;
    protected CommentService commentService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.articleService = (ArticleService) container.getBean("articleService");
        this.commentService = (CommentService) container.getBean("commentService");
    }
}