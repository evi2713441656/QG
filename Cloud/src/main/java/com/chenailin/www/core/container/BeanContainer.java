package com.chenailin.www.core.container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author evi
 */
public class BeanContainer {
    // 使用静态常量保存单例实例
    private static final BeanContainer INSTANCE = new BeanContainer();
    private final Map<String, Object> beans = new HashMap<>();

    // 私有构造函数，防止外部实例化
    private BeanContainer() {}

    // 获取单例实例的静态方法
    public static BeanContainer getInstance() {
        return INSTANCE;
    }

    public void registerBean(String beanName, Object bean) {
        beans.put(beanName, bean);
    }

    public Object getBean(String beanName) {
        return beans.get(beanName);
    }
}