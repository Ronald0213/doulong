package com.hotyi.hotyi.other.hotyiClass;

/**
 * Created by HOTYI on 2017/8/21.
 */

public class LoginInfo {
    int code ;
    String return_msg;
    String result_msg;
    LoginData loginData;

    public LoginInfo(){}

    public LoginInfo(int mcode,String mreturn_msg,String mresult_msg,LoginData mloginData){
        this.code = mcode;
        this.result_msg = mresult_msg;
        this.return_msg = mreturn_msg;
        this.loginData = mloginData;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getReturn_msg() {
        return return_msg;
    }

    public void setReturn_msg(String return_msg) {
        this.return_msg = return_msg;
    }

    public String getResult_msg() {
        return result_msg;
    }

    public void setResult_msg(String result_msg) {
        this.result_msg = result_msg;
    }

    public LoginData getLoginData() {
        return loginData;
    }

    public void setLoginData(LoginData loginData) {
        this.loginData = loginData;
    }
}
