package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/8/31.
 */

public class OfficialInfo {

    String userId;
    String userName;
    String headImage;
    int msex;

    public OfficialInfo(String userId, String userName, String headImage, int msex) {
        this.userId = userId;
        this.userName = userName;
        this.headImage = headImage;
        this.msex = msex;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public int getMsex() {
        return msex;
    }

    public void setMsex(int msex) {
        this.msex = msex;
    }
}
