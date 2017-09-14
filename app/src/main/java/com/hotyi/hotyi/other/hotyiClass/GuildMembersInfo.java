package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/14.
 */

public class GuildMembersInfo {

    String name;
    String headImg;
    String userId;
    String identity;

    public GuildMembersInfo(String name, String headImg, String userId, String identity) {
        this.name = name;
        this.headImg = headImg;
        this.userId = userId;
        this.identity = identity;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

