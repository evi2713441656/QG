package com.chenailin.www.model.vo;

import java.util.Date;

/**
 * @author evi
 */
public class EnterpriseVO {
    private Long id;
    private String name;
    private Long creatorId;
    private String creatorName;
    private String creatorAvatar;
    private String roleName;
    private Integer memberCount;
    private Integer currentUserRole;
    private Date createTime;
    private Date updateTime;

    public EnterpriseVO() {
    }

    public EnterpriseVO(Date createTime, String creatorAvatar, Long creatorId, String creatorName, Integer currentUserRole, Long id, Integer memberCount, String name, String roleName, Date updateTime) {
        this.createTime = createTime;
        this.creatorAvatar = creatorAvatar;
        this.creatorId = creatorId;
        this.creatorName = creatorName;
        this.currentUserRole = currentUserRole;
        this.id = id;
        this.memberCount = memberCount;
        this.name = name;
        this.roleName = roleName;
        this.updateTime = updateTime;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getCurrentUserRole() {
        return currentUserRole;
    }

    public void setCurrentUserRole(Integer currentUserRole) {
        this.currentUserRole = currentUserRole;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}