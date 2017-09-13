package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/13.
 */

public class UserSignRankInfo {
    String name;
    String headImg;
    String days;

    public UserSignRankInfo(String name, String headImg, String days) {
        this.name = name;
        this.headImg = headImg;
        this.days = days;
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

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }
}
