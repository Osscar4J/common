package com.zhao.common.constants;

/**
 * 角色
 * @Author: zhaolianqi
 * @Date: 2020/10/30 14:39
 * @Version: v1.0
 */
public enum RoleEnums {

    ADMIN(1, "系统管理员"),
    ORG_ADMIN(2, "机构管理员"),
    BUSINESS_MAN(3, "业务员"),
    ;

    RoleEnums(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    private final int code;
    private final String msg;

    public int getCode(){
        return code;
    }

    public String getMsg(){
        return msg;
    }

}
