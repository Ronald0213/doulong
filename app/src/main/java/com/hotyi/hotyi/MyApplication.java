package com.hotyi.hotyi;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mob.MobApplication;
import com.mob.MobSDK;
import com.mob.commons.SHARESDK;
import com.tencent.tauth.Tencent;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.framework.utils.ShareSDKR;
import cn.sharesdk.sina.weibo.SinaWeibo;
import io.rong.imkit.RongIM;

/**
 * Created by HOTYI on 2017/8/15.
 */

public class MyApplication extends MobApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        RongIM.init(this);
        SealAppContext.init(this);
        MobSDK.init(this,"1f9cdda5e539b","b8200c70664deeb5a465588626514531");

//        HashMap<String,Object> hashMap = new HashMap<String, Object>();
//        hashMap.put("Id","1");
//        hashMap.put("SortId","1");
//        hashMap.put("AppKey","2667152278");
//        hashMap.put("AppSecret","518d44639901136b42198e2f627a7595");
//        hashMap.put("RedirectUrl","http://2blong.com/");
//        hashMap.put("ShareByAppClient","true");
//        hashMap.put("Enable","true");
//        ShareSDK.setPlatformDevInfo(SinaWeibo.NAME,hashMap);
    }

}
