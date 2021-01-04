package com.zhao.common.model;

import com.zhao.common.entity.UserInfo;

import java.io.Serializable;

public class TokenModel implements Serializable {

    private static final long serialVersionUID = -2126466461380912711L;
    private String token;
    private UserInfo user;

    public UserInfo getUser() {
        return user;
    }

    public TokenModel setUser(UserInfo user) {
        this.user = user;
        return this;
    }

    public String getToken() {
        return token;
    }

    public TokenModel setToken(String token) {
        this.token = token;
        return this;
    }
}
