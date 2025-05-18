package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.Enterprise;

import java.util.List;

/**
 * @author evi
 */
public interface EnterpriseDao {
    Enterprise findById(Long id);
    List<Enterprise> findByCreatorId(Long creatorId);
    List<Enterprise> findByUserId(Long userId);
    void save(Enterprise enterprise);
    void update(Enterprise enterprise);
    void delete(Long id);
    boolean exists(String name);
}