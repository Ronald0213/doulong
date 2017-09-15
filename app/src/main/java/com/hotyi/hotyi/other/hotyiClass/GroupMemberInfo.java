package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/9/15.
 */

public class GroupMemberInfo {
    String id;
    String headImg;
    String name;

    public GroupMemberInfo(String id, String headImg, String name) {
        this.id = id;
        this.headImg = headImg;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
