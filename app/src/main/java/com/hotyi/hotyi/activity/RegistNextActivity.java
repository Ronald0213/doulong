package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.app.Notification;
import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.HotyiClass.LoginData;
import com.hotyi.hotyi.other.HotyiClass.LoginInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.async.OnDataListener;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class RegistNextActivity extends MyBaseActivity implements OnDataListener {

    private String phone_num = null;
    private static final int REGIST = 25;
    Handler myHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_next);
        Intent intent = getIntent();
        phone_num = intent.getStringExtra("phone");
        mAsyncTaskManager.request(REGIST, true, this);
    }



    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case REGIST:
                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.SendRegistCode;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("PhoneNum", phone_num);
                    if(phone_num == null){
                        Log.e("RSA",".......");
                    }
                    else
                        Log.e("RSA","----   "+phone_num.toString());
                    Log.e("RSA","++++   "+phone_num);
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                } catch (Exception e) {

                }
                break;
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        super.onSuccess(requestCode, result);
        switch (requestCode){
            case REGIST:
                try {
                    String str = result.toString();
                    JSONObject jsonObject = new JSONObject(str);
                    final LoginInfo myLoginInfo = new LoginInfo();
                    myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
                    myLoginInfo.setResult_msg(jsonObject.get("result_msg").toString());
                    myLoginInfo.setReturn_msg(jsonObject.get("retrun_msg").toString());
                    JSONObject logindataJson = new JSONObject(jsonObject.get("data").toString());
                    if (myLoginInfo.getCode() == -1){
                        Toast.makeText(RegistNextActivity.this,"Test",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    LoginData loginData = new LoginData();
                    loginData.setRYToken(logindataJson.getString("RYToken").toString());
                    loginData.setNickName(logindataJson.get("NickName").toString());
                    loginData.setHeadImageUrl(logindataJson.get("HeadImage").toString());
                    loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                    myLoginInfo.setLoginData(loginData);
                    if (myLoginInfo.getCode() == 1) {
                        String token = myLoginInfo.getLoginData().getRYToken();
                        Log.e("login",token);

                    }
                }catch (Exception e){
                    Log.e("LOGIN","  " +e.toString());
                }
                break;


        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {
        super.onFailure(requestCode, state, result);
    }
}
