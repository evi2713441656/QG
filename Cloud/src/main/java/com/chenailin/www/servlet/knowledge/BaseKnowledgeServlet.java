package com.chenailin.www.servlet.knowledge;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.core.servlet.BaseServlet;
import com.chenailin.www.service.KnowledgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * 知识库相关Servlet的基类
 * @author evi
 */
public abstract class BaseKnowledgeServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseKnowledgeServlet.class);

    protected KnowledgeService knowledgeService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.knowledgeService = (KnowledgeService) container.getBean("knowledgeService");
    }
}