package com.zhao.common.utils.ai;

/**
 * AccessToken
 * @Author: zhaolianqi
 * @Date: 2021/7/13 11:27
 * @Version: v1.0
 */
public class AIAccessToken {

    private String accessToken;
    private int exp;
    private int st;

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public int getSt() {
        return st;
    }

    public void setSt(int st) {
        this.st = st;
    }
}
