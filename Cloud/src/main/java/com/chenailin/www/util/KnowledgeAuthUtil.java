package com.chenailin.www.util;

import com.chenailin.www.dao.KnowledgeMemberDao;
import com.chenailin.www.daoimpl.KnowledgeMemberDaoImpl;
import com.chenailin.www.model.enums.KnowledgeRole;
import com.chenailin.www.model.pojo.KnowledgeMember;

/**
 * @author evi
 */
public class KnowledgeAuthUtil {
    private static final KnowledgeMemberDao MEMBER_DAO = new KnowledgeMemberDaoImpl();

    public static void checkPermission(Long knowledgeId, Long userId, KnowledgeRole requiredRole) {
        KnowledgeMember member = MEMBER_DAO.findByKnowledgeAndUser(knowledgeId, userId);
        if (member == null) {
            throw new RuntimeException("无权限操作该知识库");
        }

        if (member.getRole() > requiredRole.code) {
            throw new RuntimeException("需要" + requiredRole.desc + "权限");
        }
    }

}