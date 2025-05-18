package com.chenailin.www.dao;

import com.chenailin.www.model.pojo.EnterpriseMember;
import java.util.List;

/**
 * Data Access Object interface for enterprise member operations
 * @author evi
 */
public interface EnterpriseMemberDao {
    EnterpriseMember findById(Long id);
    EnterpriseMember findByEnterpriseAndUser(Long enterpriseId, Long userId);
    List<EnterpriseMember> findByEnterpriseId(Long enterpriseId);
    int countMembers(Long enterpriseId);
    boolean exists(Long enterpriseId, Long userId);
    void save(EnterpriseMember member);
    void update(EnterpriseMember member);
    void delete(Long id);
    void deleteByEnterpriseId(Long enterpriseId);
    List<EnterpriseMember> findByUserIdAndRole(Long userId, int role);
    List<EnterpriseMember> findByDepartmentId(Long departmentId);
}