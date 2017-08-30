package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/8/21.
 */

public class LoginData {
    String RYToken;
    String NickName;
    String HeadImageUrl;
    String AccountInfo;
    String Token;

    public void setToken(String token) {
        Token = token;
    }

    public String getToken() {
        return Token;
    }

    public String getRYToken() {
        return RYToken;
    }

    public void setRYToken(String RYToken) {
        this.RYToken = RYToken;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getHeadImageUrl() {
        return HeadImageUrl;
    }

    public void setHeadImageUrl(String headImageUrl) {
        HeadImageUrl = headImageUrl;
    }

    public String getAccountInfo() {
        return AccountInfo;
    }

    public void setAccountInfo(String accountInfo) {
        AccountInfo = accountInfo;
    }

}
