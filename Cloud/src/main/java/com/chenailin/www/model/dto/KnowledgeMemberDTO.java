package com.chenailin.www.model.dto;

import javax.validation.constraints.NotNull;

/**
 * 知识库成员数据传输对象
 * @author evi
 */
public class KnowledgeMemberDTO {
    @NotNull(message = "知识库ID不能为空")
    private Long knowledgeId;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotNull(message = "角色不能为空")
    // 对应KnowledgeRole的code
    private Integer role;

    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
    }
}