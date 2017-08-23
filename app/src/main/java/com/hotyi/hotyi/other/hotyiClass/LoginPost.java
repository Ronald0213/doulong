package com.hotyi.hotyi.other.hotyiClass;

import java.io.Serializable;

/**
 * Created by HOTYI on 2017/8/22.
 */

public class LoginPost implements Serializable {

    String RSAText;

    public void setRSAText(String RSAText) {
        this.RSAText = RSAText;
    }

    public String getRSAText() {
        return RSAText;
    }
}
