package com.chenailin.www.service;

import com.chenailin.www.model.dto.EnterpriseDepartmentDTO;
import com.chenailin.www.model.pojo.EnterpriseDepartment;
import com.chenailin.www.model.vo.EnterpriseDepartmentVO;
import com.chenailin.www.model.vo.EnterpriseMemberVO;

import java.util.List;

/**
 * Enterprise Department Service Interface
 * @author evi
 */
public interface EnterpriseDepartmentService {

    EnterpriseDepartment createDepartment(EnterpriseDepartmentDTO dto, Long userId);

    EnterpriseDepartment updateDepartment(EnterpriseDepartmentDTO dto, Long userId);

    void deleteDepartment(Long id, Long userId);

    EnterpriseDepartmentVO getDepartmentById(Long id, Long userId);

    List<EnterpriseDepartmentVO> listDepartments(Long enterpriseId, Long userId);

    List<EnterpriseDepartmentVO> listManagedDepartments(Long userId);

    List<EnterpriseMemberVO> getDepartmentMembers(Long departmentId, Long userId);

    void addMember(Long departmentId, Long userId, Long operatorId);

    void removeMember(Long departmentId, Long userId, Long operatorId);

    void changeManager(Long departmentId, Long newManagerId, Long userId);
}