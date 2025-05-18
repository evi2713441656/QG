package com.chenailin.www.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import com.chenailin.www.config.BeanConfig;

/**
 * @author evi
 */
@WebListener
public class BeanConfigListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 在应用启动时执行
        System.out.println("应用启动中，初始化 Bean 容器...");
        BeanConfig.configureBeans();
    }
}