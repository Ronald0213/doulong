package com.hotyi.hotyi.utils.async;

import com.squareup.okhttp.OkHttpClient;

/**
 * Created by HOTYI on 2017/8/22.
 */

public class OKHttpUtil {

    private OkHttpClient okHttpClient;
    private static OKHttpUtil instance;
    private OKHttpUtil(){
        init();
    }
    public static OKHttpUtil getInstancec(){
        if (instance == null){
            return new OKHttpUtil();
        }else {
            return instance;
        }
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    private void init(){
        okHttpClient = new OkHttpClient();
    }
}
