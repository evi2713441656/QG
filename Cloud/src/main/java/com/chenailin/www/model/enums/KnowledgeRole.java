package com.chenailin.www.model.enums;

/**
 * @author evi
 */

public enum KnowledgeRole {
    OWNER(1, "所有者"),
    ADMIN(2, "管理员"),
    MEMBER(3, "成员");

    public final int code;
    public final String desc;

    KnowledgeRole(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

}