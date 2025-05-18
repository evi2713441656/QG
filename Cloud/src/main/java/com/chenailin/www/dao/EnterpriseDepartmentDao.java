package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.EnterpriseDepartment;
import java.util.List;

/**
 * Data Access Object interface for enterprise department operations
 * @author evi
 */
public interface EnterpriseDepartmentDao {
    EnterpriseDepartment findById(Long id);
    List<EnterpriseDepartment> findByEnterpriseId(Long enterpriseId);
    List<EnterpriseDepartment> findByManagerId(Long userId);
    boolean nameExists(Long enterpriseId, String name, Long excludeId);
    void save(EnterpriseDepartment department);
    void update(EnterpriseDepartment department);
    void delete(Long id);
    void deleteByEnterpriseId(Long enterpriseId);
}