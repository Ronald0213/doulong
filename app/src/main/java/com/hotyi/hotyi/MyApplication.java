package com.hotyi.hotyi;

import android.app.Application;

import io.rong.imkit.RongIM;

/**
 * Created by HOTYI on 2017/8/15.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
    }
}
