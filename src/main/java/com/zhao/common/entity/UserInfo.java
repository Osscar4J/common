package com.zhao.common.entity;

import java.io.Serializable;

/**
 * userInfo
 * @Author: zhaolianqi
 * @Date: 2020/12/7 11:27
 * @Version: v1.0
 */
public class UserInfo implements Serializable {

    private Integer id;
    private Integer roleId;

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
