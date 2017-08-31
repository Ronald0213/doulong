package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/8/31.
 */

public class MyUserInfo {


    private String userId;
    private String nickName;
    private String headImage;
    private String ryAccount;
    private String phoneNum;
    private String sex;
    private static  MyUserInfo instance ;

    public static MyUserInfo getInstance(){
        if (instance == null)
            instance = new MyUserInfo();
        return instance;
    }



    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getRyAccount() {
        return ryAccount;
    }

    public void setRyAccount(String rYAccount) {
        this.ryAccount = rYAccount;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }


}
