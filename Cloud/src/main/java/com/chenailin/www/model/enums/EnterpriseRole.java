package com.chenailin.www.model.enums;

/**
 * Enum for enterprise roles
 * @author evi
 */
public enum EnterpriseRole {
    OWNER(1, "所有者"),
    ADMIN(2, "管理员"),
    DEPARTMENT_MANAGER(3, "部门主管"),
    PROJECT_MANAGER(4, "项目主管"),
    MEMBER(5, "普通成员");

    public final int code;
    public final String name;

    EnterpriseRole(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(int code) {
        for (EnterpriseRole role : EnterpriseRole.values()) {
            if (role.code == code) {
                return role.name;
            }
        }
        return "未知角色";
    }

    public static EnterpriseRole getByCode(int code) {
        for (EnterpriseRole role : EnterpriseRole.values()) {
            if (role.code == code) {
                return role;
            }
        }
        return null;
    }
}