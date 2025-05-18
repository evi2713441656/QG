package com.chenailin.www.service;

import com.chenailin.www.model.dto.EnterpriseDTO;
import com.chenailin.www.model.dto.EnterpriseMemberDTO;
import com.chenailin.www.model.pojo.Enterprise;
import com.chenailin.www.model.vo.EnterpriseMemberVO;
import com.chenailin.www.model.vo.EnterpriseVO;

import java.util.List;

/**
 * Enterprise Service Interface
 * @author evi
 */
public interface EnterpriseService {

    Enterprise createEnterprise(EnterpriseDTO dto, Long creatorId);

    Enterprise updateEnterprise(EnterpriseDTO dto, Long userId);

    void deleteEnterprise(Long id, Long userId);

    EnterpriseVO getEnterpriseById(Long id, Long userId);

    List<EnterpriseVO> getUserEnterprises(Long userId);

    List<EnterpriseVO> getCreatedEnterprises(Long creatorId);

    List<EnterpriseVO> getOwnedEnterprises(Long userId);

    List<EnterpriseMemberVO> getEnterpriseMembers(Long enterpriseId, Long userId);

    void inviteMember(Long enterpriseId, EnterpriseMemberDTO dto, Long userId);

    void joinEnterprise(Long enterpriseId, Long userId);

    void updateMemberRole(Long memberId, EnterpriseMemberDTO dto, Long userId);

    void updateMemberDepartment(Long memberId, Long departmentId, Long userId);

    void leaveEnterprise(Long enterpriseId, Long userId);

    void removeMember(Long enterpriseId, Long memberId, Long userId);

    void transferOwnership(Long enterpriseId, Long newOwnerId, Long userId);
}