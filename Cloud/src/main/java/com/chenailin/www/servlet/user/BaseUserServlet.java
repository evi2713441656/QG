package com.chenailin.www.servlet.user;

import com.chenailin.www.core.container.BeanContainer;
import com.chenailin.www.core.servlet.BaseServlet;
import com.chenailin.www.service.BrowseHistoryService;
import com.chenailin.www.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

/**
 * 用户相关Servlet的基类
 * @author evi
 */
public abstract class BaseUserServlet extends BaseServlet {
    private static final Logger logger = LoggerFactory.getLogger(BaseUserServlet.class);

    protected BrowseHistoryService browseHistoryService;
    protected UserService userService;

    @Override
    public void init() throws ServletException {
        super.init();
        BeanContainer container = BeanContainer.getInstance();
        this.userService = (UserService) container.getBean("userService");
        this.browseHistoryService = (BrowseHistoryService) container.getBean("browseHistoryService");
    }
}