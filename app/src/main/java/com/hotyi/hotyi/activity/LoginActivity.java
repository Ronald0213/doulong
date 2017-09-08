package com.hotyi.hotyi.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.LoginData;
import com.hotyi.hotyi.other.hotyiClass.LoginInfo;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.net.URL;
import java.util.HashMap;


import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.UserInfo;

public class LoginActivity extends MyBaseActivity implements View.OnClickListener, OnDataListener {

    private MyUserInfo myUserInfo;
    private AlertDialog dialog;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static final int MAX_ENCRYPT_BLOCK = 1024;
    private StringBuffer stringBuffer,autoText;
    public static final int LOGIN = 20;
    public static final int AUTO_LOGIN = 21;
    public static final int GET_TOKEN = 22;
    public static final int THIRD_LOGIN = 23;
    //    public static final int qq_LOGIN = 22;
//    public static final int WEIBO_LOGIN = 22;
    private String password;
    private String phone_num;
    private Button btn_login;
    private TextView text_forget_password;
    private TextView text_regist;
    private TextView login_agree;
    private ImageButton imgbtn_qq;
    private ImageButton imgbtn_weixin;
    private ImageButton imgbtn_weibo;
    private ImageView imgbtn_choose;
    private EditText edt_phone_num;
    private EditText edt_password;
    public AsyncTaskManager mAsyncTaskManager;
    public String android_id;
    private String key_str = null;
    private ImageView sinaImageView, qqImageView, weixinImageView;
    private String token = null;
    private int flag = 0;

    private boolean isChoose = true;
    private Bitmap bitmap_choose, bitmap_non_choose;
//    private HashMap <String,String> autoMap =  new HashMap<>();

    String device_model = Build.MODEL; // 设备型号
    String deviceBrand= android.os.Build.MANUFACTURER;
    String app_code ;
    /**
     * RSA加密
     */
    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    public static final int DEFAULT_KEY_SIZE = 2048;//秘钥默认长度
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();// 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
    public static final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数

    //微信注册
    private final String APP_ID = "wx4ae76f1d5b08feb3";
    private IWXAPI api;

    private void regToWx() {
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);
        api.registerApp(APP_ID);
    }

    private void getCode() {
        final SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "carjob_wx_login";
        api.sendReq(req);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            PackageManager packageManager = this.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(this.getPackageName(), 0);
            app_code = String.valueOf(packageInfo.versionCode);
        }catch (Exception e){

        }
        regToWx();
        myUserInfo = MyUserInfo.getInstance();
        BitmapDrawable bitmap_choose_drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.home_choosefriends_yes_3x);
        bitmap_choose = bitmap_choose_drawable.getBitmap();
        BitmapDrawable bitmap_non_choose_drawable = (BitmapDrawable) getResources().getDrawable(R.mipmap.home_choosefriends_no3x);
        bitmap_non_choose = bitmap_non_choose_drawable.getBitmap();
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("AccountInformation", null);
        editor.apply();

        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());
        initView();
        autoLogin();

    }
    private void autoLogin(){
        Log.e("auto done", "    1  ");
        String autoUserID = sharedPreferences.getString("userId", null);
        Log.e("auto done", "    1  " +autoUserID);
        String autoName = sharedPreferences.getString("name",null);
        Log.e("auto done", "    1  "+autoName);
        String autoRongAccount = sharedPreferences.getString("ryAccount",null);
        Log.e("auto done", "    1  "+autoRongAccount);
        String autoHeadImage = sharedPreferences.getString("headImage",null);
        Log.e("auto done", "    1  "+autoHeadImage);
        Log.e("auto done", "    2 ");
        if (autoUserID != null  && autoName != null  && autoRongAccount != null &&autoHeadImage != null ){
            Log.e("auto done", "    3 ");
//            autoText.append("DeviceBrand=").append(deviceBrand).append("&")
//                    .append("DeviceId=").append(android_id).append("&")
//                    .append("DeviceModel=").append(device_model).append("&")
//                    .append("OS=").append("Android").append("&")
//                    .append("Platform=").append("APP").append("&")
//                    .append("UserId=").append(autoUserID).append("&")
//                    .append("Version=").append(app_code);
//            autoMap.put("DeviceBrand",deviceBrand);
//            autoMap.put("DeviceId",android_id);
//            autoMap.put("DeviceModel",device_model);
//            autoMap.put("OS","Android");
//            autoMap.put("Platform","APP");
//            autoMap.put("UserId",autoUserID);
//            autoMap.put("Version",app_code);
            myUserInfo.setNickName(autoName);
            myUserInfo.setHeadImage(autoHeadImage);
            myUserInfo.setRyAccount(autoRongAccount);
            myUserInfo.setUserId(autoUserID);
            Log.e("auto done", "    4  ");
            mAsyncTaskManager.request(AUTO_LOGIN,true,this);
        }
        Log.e("auto done", "    5  ");
//        editor.putString("userId",myUserInfo.getUserId());
//        editor.putString("ryAccount",myUserInfo.getRyAccount());
//        editor.putString("name",myUserInfo.getNickName());
//        editor.putString("ryToken",ryToken);
//        editor.putString("loginToken",loginData.getToken());
    }

    public void initView() {
        btn_login = (Button) findViewById(R.id.btn_login);
        text_forget_password = (TextView) findViewById(R.id.text_forget_password);
        text_regist = (TextView) findViewById(R.id.text_regist);
        edt_password = (EditText) findViewById(R.id.edt_password);
        edt_phone_num = (EditText) findViewById(R.id.edt_phone_num);
        imgbtn_qq = (ImageButton) findViewById(R.id.imgbtn_qq);
        imgbtn_weibo = (ImageButton) findViewById(R.id.imgbtn_weibo);
        imgbtn_weixin = (ImageButton) findViewById(R.id.imgbtn_weixin);
        sinaImageView = (ImageView) findViewById(R.id.imgbtn_weibo);
        imgbtn_choose = (ImageView) findViewById(R.id.login_choose);
        login_agree = (TextView) findViewById(R.id.login_agree);


        imgbtn_choose.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        text_regist.setOnClickListener(this);
        text_forget_password.setOnClickListener(this);
        imgbtn_qq.setOnClickListener(this);
        imgbtn_weibo.setOnClickListener(this);
        imgbtn_weixin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login_choose:
                if (isChoose) {
                    imgbtn_choose.setImageBitmap(bitmap_non_choose);
                    isChoose = false;
                    btn_login.setEnabled(false);
                    btn_login.setBackgroundResource(R.drawable.login_unclick_shape);
                } else {
                    imgbtn_choose.setImageBitmap(bitmap_choose);
                    isChoose = true;
                    btn_login.setEnabled(true);
                    btn_login.setBackgroundResource(R.drawable.login_btn_shape);
                }
                break;
            case R.id.login_agree:
                break;
            case R.id.btn_login:

                phone_num = edt_phone_num.getText().toString();
                password = edt_password.getText().toString();
                if (TextUtils.isEmpty(phone_num)) {
                    Toast.makeText(LoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG);
                    return;
                }
                try {
                    stringBuffer = new StringBuffer();
                    key_str = RSAUtil.encryptByPublicKey(stringBuffer.append(android_id).append("&").append(phone_num).append("&").append(password).append("&").append(System.currentTimeMillis()).toString());
//                    Log.e("aaaaaaaaaaaaaaaaaaaa","   "+ stringBuffer.toString());
//                    String str = RSAUtil.decryptByPrivateKey(key_str);
//                    Log.e("aaaaaaaaaaaaaaaaaaaa","ssssssss"   +str);
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                    Log.e("RSA0",e.toString());
                    break;
                }
                mAsyncTaskManager.request(LOGIN, true, this);
//                Log.e("LOGIN", "CLICK text run");
                break;
            case R.id.text_forget_password:
                //  startActivity(new Intent(LoginActivity.this,Forg));
                break;
            case R.id.text_regist:
                startActivityForResult(new Intent(LoginActivity.this, RegistActivity.class), 20);
                break;
            case R.id.imgbtn_qq:
                thirdQQLogin();
                break;
            case R.id.imgbtn_weibo:
                ThirdWeiboLogin();
                break;
            case R.id.imgbtn_weixin:
                getCode();
                //******微信登录
//                final SendAuth.Req req = new SendAuth.Req();
//                req.scope = "snsapi_userinfo";
//                req.state = "diandi_wx_login";
//                api.sendReq(req);
//                startActivityForResult(new Intent(LoginActivity.this, WXEntryActivity.class), 20);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case LOGIN:
                break;
            case GET_TOKEN:
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
//        Log.e("LOGIN", "CLICK text run  do in background");
        switch (requestCode) {
            case AUTO_LOGIN:

                try {
                    Log.e("auto done", "    6  ");
                    String login_url = MyAsynctask.HOST + MyAsynctask.ValidateToken;
                    String userId = myUserInfo.getUserId();
                    String loginToken = sharedPreferences.getString("loginToken",null);
                    if (loginToken == null)
                        return null;
//                    String autoRSAtext = RSAUtil.encryptByPrivateKey(userId);
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("UserId", userId);
                    map.put("Token",loginToken);
//                    if (key_str == null) {
//                        Log.e("RSA", ".......");
//                    } else
//                        Log.e("RSA", "----   " + stringBuffer.toString());
//                    Log.e("RSA", "++++   " + key_str);
                    Log.e("auto done", "    7  ");
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                } catch (Exception e) {

                }


                break;
            case LOGIN:

                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.Login;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RSAText", key_str);
                    if (key_str == null) {
                        Log.e("RSA", ".......");
                    } else
                        Log.e("RSA", "----   " + stringBuffer.toString());
                    Log.e("RSA", "++++   " + key_str);
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

        Log.e("LOGIN", "CLICK text run onSuccess    ------" + result.toString());
        if (result != null) {
            switch (requestCode) {

                case AUTO_LOGIN:

                    if (result != null){
                        try{
                            Log.e("auto done", "    8  ");
                            String rongToken = sharedPreferences.getString("ryToken",null);
                            String autoResult = result.toString();
                            JSONObject autoJson = new JSONObject(autoResult);
                            if (autoJson.getInt("code") == 1){
                                Log.e("auto done", "    9  ");
//                                String autoInfo = RSAUtil.decryptByPrivateKey(autoJson.getJSONObject("data").getString("RSAText"));
//                                Log.e("auto done", "    9  " + autoInfo);
//                                JSONObject auto = new JSONObject(autoInfo);
//                                editor.putString("loginToken",auto.getString("Token"));
                                String autoToken = autoJson.getString("data");
                                editor.putString("loginToken",autoToken);
                                editor.commit();
                                Log.e("auto done", "    10  ");
                                RongIM.connect(rongToken, new RongIMClient.ConnectCallback() {
                                    @Override
                                    public void onTokenIncorrect() {

                                    }

                                    @Override
                                    public void onSuccess(String s) {
                                        Toast.makeText(LoginActivity.this,"自动登录成功",Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onError(RongIMClient.ErrorCode errorCode) {

                                    }
                                });
                                RongIM.getInstance().refreshUserInfoCache(new UserInfo(myUserInfo.getRyAccount(), myUserInfo.getNickName(), Uri.parse(myUserInfo.getHeadImage())));
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();

                            }
                        }catch (Exception e){

                        }
                    }

                    break;

                case LOGIN:
                    try {
                        Log.e("LOGIN", "CLICK text run onSuccess" + "-------------");
                        String str = result.toString();
                        JSONObject jsonObject = new JSONObject(str);
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"111111");
                        Log.e("LOGIN", "   result   " + str);
                        final LoginInfo myLoginInfo = new LoginInfo();
                        myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"2222222");
                        myLoginInfo.setResult_msg(jsonObject.get("result_msg").toString());
                        myLoginInfo.setReturn_msg(jsonObject.get("retrun_msg").toString());
                        JSONObject logindataJson = new JSONObject(jsonObject.get("data").toString());
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"3333334");
                        if (myLoginInfo.getCode() == -1) {
                            Toast.makeText(LoginActivity.this, "Test", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        final LoginData loginData = new LoginData();
                        loginData.setRYToken(logindataJson.getString("RYToken").toString());
                        loginData.setNickName(logindataJson.get("NickName").toString());
                        myUserInfo.setNickName(loginData.getNickName());
                        loginData.setHeadImageUrl(logindataJson.get("HeadImage").toString());
                        myUserInfo.setHeadImage(loginData.getHeadImageUrl());
                        loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                        loginData.setToken(logindataJson.get("Token").toString());
                        myLoginInfo.setLoginData(loginData);
                        if (myLoginInfo.getCode() == 1) {
                            Log.e("LOGIN", "   " + "login done");
                            String userInfo = RSAUtil.decryptByPrivateKey(logindataJson.get("AccountInformation").toString());
                            JSONObject userJson = new JSONObject(userInfo);
                            Log.e("userAccountInfo   :", userInfo);
                            myUserInfo.setUserId(userJson.getString("UserId"));
                            myUserInfo.setRyAccount(userJson.getString("RYAccount"));
                            final String ryToken = myLoginInfo.getLoginData().getRYToken();
                            Log.e("LOGIN   ", myUserInfo.getUserId());
                            RongIM.connect(ryToken, new RongIMClient.ConnectCallback() {

                                @Override
                                public void onSuccess(String s) {
                                    editor.putString("userId",myUserInfo.getUserId());
                                    editor.putString("ryAccount",myUserInfo.getRyAccount());
                                    editor.putString("name",myUserInfo.getNickName());
                                    editor.putString("ryToken",ryToken);
                                    editor.putString("loginToken",loginData.getToken());
                                    editor.putString("headImage",myUserInfo.getHeadImage());
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    String msg = myLoginInfo.getResult_msg();
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onTokenIncorrect() {

                                }
                            });
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(myUserInfo.getRyAccount(), myUserInfo.getNickName(), Uri.parse(myUserInfo.getHeadImage())));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        }
                    } catch (Exception e) {
                        Log.e("LOGIN", "  " + e.toString());
                    }
                    break;
                case GET_TOKEN:
                    break;
            }
        } else {
            Log.e("LOGIN", "CLICK text run onSuccess" + "   null");
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String usertoken = intent.getStringExtra("unionid");
        String gendle = intent.getStringExtra("gendle");
        String headImg = intent.getStringExtra("headImg");
        if (name != null && usertoken != null && gendle != null && headImg != null) {
            token = usertoken;
            flag = 1;
            myUserInfo.setHeadImage(headImg);
            myUserInfo.setNickName(name);
            if (!gendle.equals("1"))
                myUserInfo.setSex("2");
            myUserInfo.setSex(gendle);
            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
        }
        else
            return;
//        startActivity(new Intent(LoginActivity.this,MyDialogActivity.class));

    }

    OnDataListener mydataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode) {
                case THIRD_LOGIN:

                    try {

                        String login_url = MyAsynctask.HOST + MyAsynctask.ThirdLogin;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UnionNo", token);
                        if (flag == 0)
                            return null;
                        else
                            map.put("Type", String.valueOf(flag));
                        if (key_str == null) {
                            Log.e("RSA", ".......");
                        } else
                            Log.e("RSA", "----   " + flag);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);

                    } catch (Exception e) {

                    }

                    break;
            }
            return null;
        }

        @Override
        public void onSuccess(int requestCode, Object result) {

            switch (requestCode) {
                case THIRD_LOGIN:
                    try {
                        Log.e("LOGIN", "CLICK text run onSuccess" + "-------------");
                        String str = result.toString();
                        Log.e("LOGIN", "  " + str);
                        JSONObject jsonObject = new JSONObject(str);
                        if (jsonObject.getInt("code") == 1 && jsonObject.getString("retrun_msg").equals("HasBind")) {
                            JSONObject logindataJson = new JSONObject(jsonObject.get("data").toString());
                            final LoginInfo myLoginInfo = new LoginInfo();
                            final LoginData loginData = new LoginData();
                            loginData.setRYToken(logindataJson.getString("RYToken").toString());
                            loginData.setNickName(logindataJson.get("NickName").toString());
                            myUserInfo.setNickName(loginData.getNickName());
                            loginData.setHeadImageUrl(logindataJson.get("HeadImage").toString());
                            myUserInfo.setHeadImage(loginData.getHeadImageUrl());
                            loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                            loginData.setToken(logindataJson.get("Token").toString());
                            myLoginInfo.setLoginData(loginData);
                            String userInfo = RSAUtil.decryptByPrivateKey(logindataJson.get("AccountInformation").toString());
                            JSONObject userJson = new JSONObject(userInfo);
                            Log.e("userAccountInfo   :", userInfo);
                            myUserInfo.setUserId(userJson.getString("UserId"));
                            myUserInfo.setRyAccount(userJson.getString("RYAccount"));
                            final String ryToken = myLoginInfo.getLoginData().getRYToken();
                            RongIM.connect(ryToken, new RongIMClient.ConnectCallback() {

                                @Override
                                public void onSuccess(String s) {
                                    editor.putString("userId",myUserInfo.getUserId());
                                    editor.putString("ryAccount",myUserInfo.getRyAccount());
                                    editor.putString("name",myUserInfo.getNickName());
                                    editor.putString("ryToken",ryToken);
                                    editor.putString("loginToken",loginData.getToken());
                                    editor.putString("headImage",myUserInfo.getHeadImage());
                                    editor.commit();
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(RongIMClient.ErrorCode errorCode) {
                                    String msg = myLoginInfo.getResult_msg();
                                    Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onTokenIncorrect() {

                                }
                            });
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(myUserInfo.getRyAccount(), myUserInfo.getNickName(), Uri.parse(myUserInfo.getHeadImage())));
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();


                        } else if (jsonObject.getInt("code") == 1 && jsonObject.getString("retrun_msg").equals("NoBind")) {
                            Intent intent = new Intent(LoginActivity.this, ThirdLoginBindActivity.class);
                            intent.putExtra("UnionNo", token);
                            intent.putExtra("Type", String.valueOf(flag));
                            startActivity(intent);
                        }
                    } catch (Exception e) {

                    }


                    break;
            }

        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {

        }
    };

    private static final int WEIBO_MSG_ACTION_CCALLBACK = 11;
    private static final int QQ_MSG_ACTION_CCALLBACK = 12;
    private static final int WECHAT_MSG_ACTION_CCALLBACK = 13;
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WEIBO_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1) {
                        case 1:
                            Platform pf = ShareSDK.getPlatform(SinaWeibo.NAME);
                            String id = pf.getDb().getUserId();
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            Log.e("LOGIN", "   " + "weibo login done  4  " + pf.getDb().getUserName());
                            String sex = pf.getDb().getUserGender();
                            if (sex == null)
                                myUserInfo.setSex("1");
                            else if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");
                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().get("unionid");
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 3;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this, "失败", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this, "取消", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };


    public void ThirdWeiboLogin() {
        //初始化新浪平台
        Platform pf = ShareSDK.getPlatform(SinaWeibo.NAME);
        pf.SSOSetting(true);
        //设置监听
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Message msg = new Message();
                msg.what = WEIBO_MSG_ACTION_CCALLBACK;
                msg.arg1 = 1;
                msg.arg2 = i;
                msg.obj = platform;
                Log.e("LOGIN", "   " + "weibo login done");
                myHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Message msg = new Message();
                msg.what = WEIBO_MSG_ACTION_CCALLBACK;
                msg.arg1 = 2;
                msg.arg2 = i;
                msg.obj = platform;
                myHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {

                Message msg = new Message();
                msg.what = WEIBO_MSG_ACTION_CCALLBACK;
                msg.arg1 = 3;
                msg.arg2 = i;
                msg.obj = platform;
                myHandler.sendMessage(msg);
            }
        });
        //获取登陆用户的信息，如果没有授权，会先授权，然后获取用户信息
        pf.authorize();
        pf.showUser(null);
    }

    Handler myQQHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case QQ_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1) {
                        case 1:
                            Platform pf = ShareSDK.getPlatform(QQ.NAME);
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            String sex = pf.getDb().getUserGender();
                            if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");

                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
                            Log.e("sharesdk use_icon", pf.getDb().getUserGender());
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().get("unionid");
//                            token = pf.getDb().getToken();
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this, "成功", Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 2;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this, "q失败", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this, "q取消", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };


    public void thirdQQLogin() {
        //初始化qq
        Platform pf = ShareSDK.getPlatform(QQ.NAME);
        pf.SSOSetting(true);
        //设置监听
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Message msg = new Message();
                msg.what = QQ_MSG_ACTION_CCALLBACK;
                msg.arg1 = 1;
                msg.arg2 = i;
                msg.obj = platform;
                myQQHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Message msg = new Message();
                msg.what = QQ_MSG_ACTION_CCALLBACK;
                msg.arg1 = 2;
                msg.arg2 = i;
                msg.obj = platform;
                myQQHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {

                Message msg = new Message();
                msg.what = QQ_MSG_ACTION_CCALLBACK;
                msg.arg1 = 3;
                msg.arg2 = i;
                msg.obj = platform;
                myQQHandler.sendMessage(msg);
            }
        });
        //获取登陆用户的信息，如果没有授权，会先授权，然后获取用户信息
        pf.authorize();
        pf.showUser(null);
    }


    Handler myWechatHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case WECHAT_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1) {
                        case 1:
                            Platform pf = ShareSDK.getPlatform(Wechat.NAME);
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            String sex = pf.getDb().getUserGender();
                            if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");

                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
                            Log.e("sharesdk use_icon", pf.getDb().getUserGender());
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().get("unionid");
//                            token = pf.getDb().getToken();
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this, "w成功", Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 1;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Platform pf_no = ShareSDK.getPlatform(Wechat.NAME);
                            Log.e("wechat", pf_no.toString());
                            Toast.makeText(LoginActivity.this, "w失败", Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this, "w取消", Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };


    public void thirdWechatLogin() {
        //初始化wechat
        Platform pf = ShareSDK.getPlatform(Wechat.NAME);
        pf.SSOSetting(true);
        //设置监听
        pf.setPlatformActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Message msg = new Message();
                Log.e("wechat", "  done 001");
                msg.what = WECHAT_MSG_ACTION_CCALLBACK;
                msg.arg1 = 1;
                msg.arg2 = i;
                msg.obj = platform;
                myWechatHandler.sendMessage(msg);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Message msg = new Message();
                msg.what = WECHAT_MSG_ACTION_CCALLBACK;
                msg.arg1 = 2;
                msg.arg2 = i;
                msg.obj = platform;
                Log.e("wechat", throwable.toString());
                myWechatHandler.sendMessage(msg);
            }

            @Override
            public void onCancel(Platform platform, int i) {

                Message msg = new Message();
                msg.what = WECHAT_MSG_ACTION_CCALLBACK;
                msg.arg1 = 3;
                msg.arg2 = i;
                msg.obj = platform;
                myWechatHandler.sendMessage(msg);
            }
        });
        //获取登陆用户的信息，如果没有授权，会先授权，然后获取用户信息
        pf.authorize();
        pf.showUser(null);
    }


}
