package com.hotyi.hotyi.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import org.json.JSONObject;

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
    private AlertDialog dialog ;
    private SharedPreferences sharedPreferences;
    private static final int MAX_ENCRYPT_BLOCK = 1024;
    private StringBuffer stringBuffer;
    public static final int LOGIN = 20;
    public static final int GET_TOKEN = 21;
    public static final int THIRD_LOGIN = 22;
//    public static final int qq_LOGIN = 22;
//    public static final int WEIBO_LOGIN = 22;
    private String password;
    private String phone_num;
    private Button btn_login;
    private TextView text_forget_password;
    private TextView text_regist;
    private ImageButton imgbtn_qq;
    private ImageButton imgbtn_weixin;
    private ImageButton imgbtn_weibo;
    private EditText edt_phone_num;
    private EditText edt_password;
    public AsyncTaskManager mAsyncTaskManager;
    public String android_id;
    private String key_str = null;
    private ImageView sinaImageView,qqImageView,weixinImageView;
    private String token = null;
    private int flag = 0;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        regToWx();
        myUserInfo = MyUserInfo.getInstance();
        sharedPreferences = getSharedPreferences("config", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("AccountInformation",null);
        editor.apply();
        String userID = sharedPreferences.getString("userID",null);
        String loginToken = sharedPreferences.getString("loginToken",null);
        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());
        initView();

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
        sinaImageView = (ImageView)findViewById(R.id.imgbtn_weibo);

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
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
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
                thirdWechatLogin();
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
            case LOGIN:

                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.Login;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RSAText", key_str);
                    if(key_str == null){
                        Log.e("RSA",".......");
                    }
                    else
                        Log.e("RSA","----   "+stringBuffer.toString());
                        Log.e("RSA","++++   "+key_str);
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
                case LOGIN:
                    try {
                        Log.e("LOGIN", "CLICK text run onSuccess"+"-------------");
                        String str = result.toString();
                        JSONObject jsonObject = new JSONObject(str);
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"111111");
                        Log.e("LOGIN","   result   "+ str);
                        final LoginInfo myLoginInfo = new LoginInfo();
                        myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"2222222");
                        myLoginInfo.setResult_msg(jsonObject.get("result_msg").toString());
                        myLoginInfo.setReturn_msg(jsonObject.get("retrun_msg").toString());
                        JSONObject logindataJson = new JSONObject(jsonObject.get("data").toString());
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"3333334");
                        if (myLoginInfo.getCode() == -1){
                            Toast.makeText(LoginActivity.this,"Test",Toast.LENGTH_SHORT).show();
                            return;
                        }
                        LoginData loginData = new LoginData();
                        loginData.setRYToken(logindataJson.getString("RYToken").toString());
                        loginData.setNickName(logindataJson.get("NickName").toString());
                        myUserInfo.setNickName(loginData.getNickName());
                        loginData.setHeadImageUrl(logindataJson.get("HeadImage").toString());
                        myUserInfo.setHeadImage(loginData.getHeadImageUrl());
                        loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                        loginData.setToken(logindataJson.get("Token").toString());
                        myLoginInfo.setLoginData(loginData);

                        if (myLoginInfo.getCode() == 1) {
                            Log.e("LOGIN","   " + "login done");
                            String userInfo = RSAUtil.decryptByPrivateKey(logindataJson.get("AccountInformation").toString());
                            JSONObject userJson = new JSONObject(userInfo);
                            myUserInfo.setUserId(userJson.getString("UserId"));
                            myUserInfo.setRyAccount(userJson.getString("RYAccount"));
                            String token = myLoginInfo.getLoginData().getRYToken();
                            Log.e("LOGIN   ",myUserInfo.getUserId());
                            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                                @Override
                                public void onSuccess(String s) {
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
                            RongIM.getInstance().refreshUserInfoCache(new UserInfo(myUserInfo.getRyAccount(),myUserInfo.getNickName(), Uri.parse(myUserInfo.getHeadImage())));
                            startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        }
                    }catch (Exception e){
                        Log.e("LOGIN","  " +e.toString());
                    }
                    break;
                case GET_TOKEN:
                    break;
            }
        }else{
            Log.e("LOGIN", "CLICK text run onSuccess"+"   null");
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    @Override
    protected void onStart() {
        super.onStart();
//        startActivity(new Intent(LoginActivity.this,MyDialogActivity.class));

    }

    OnDataListener mydataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode){
                case THIRD_LOGIN:

                    try{

                        String login_url = MyAsynctask.HOST + MyAsynctask.ThirdLogin;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UnionNo", token);
                        Log.e("LOGIN", "    "+token);
                        if(flag == 0)
                            return null;
                        else
                            map.put("Type", String.valueOf(flag));
                        if(key_str == null){
                            Log.e("RSA",".......");
                        }
                        else
                            Log.e("RSA","----   "+flag);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);

                    }catch (Exception e){

                    }

                    break;
            }
            return null;
        }

        @Override
        public void onSuccess(int requestCode, Object result) {

            switch (requestCode){
                case THIRD_LOGIN:
                    try {
                        Log.e("LOGIN", "CLICK text run onSuccess"+"-------------");
                        String str = result.toString();
                        Log.e("LOGIN","  " + str);
                        JSONObject jsonObject = new JSONObject(str);
                        if (jsonObject.getInt("code") == 1 && jsonObject.getString("retrun_msg").equals("HasBind")){
                            JSONObject logindataJson = new JSONObject(jsonObject.get("data").toString());
//                        Log.e("LOGIN", "CLICK text run onSuccess"+"3333334");
//                            if (myLoginInfo.getCode() == -1){
//                                Toast.makeText(LoginActivity.this,"Test",Toast.LENGTH_SHORT).show();
//                                return;
//                            }
                            final LoginInfo myLoginInfo = new LoginInfo();
                            LoginData loginData = new LoginData();
                            loginData.setRYToken(logindataJson.getString("RYToken").toString());
                            loginData.setNickName(logindataJson.get("NickName").toString());
                            myUserInfo.setNickName(loginData.getNickName());
                            loginData.setHeadImageUrl(logindataJson.get("HeadImage").toString());
                            myUserInfo.setHeadImage(loginData.getHeadImageUrl());
                            loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                            loginData.setToken(logindataJson.get("Token").toString());
                            myLoginInfo.setLoginData(loginData);
                                Log.e("LOGIN", "   " + "login done");
                                String userInfo = RSAUtil.decryptByPrivateKey(logindataJson.get("AccountInformation").toString());
                                JSONObject userJson = new JSONObject(userInfo);
                                myUserInfo.setUserId(userJson.getString("UserId"));
                                myUserInfo.setRyAccount(userJson.getString("RYAccount"));
                                String token = myLoginInfo.getLoginData().getRYToken();
                                Log.e("LOGIN   ", myUserInfo.getUserId());
                                RongIM.connect(token, new RongIMClient.ConnectCallback() {

                                    @Override
                                    public void onSuccess(String s) {
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


                        }else if (jsonObject.getInt("code") == 1 && jsonObject.getString("retrun_msg").equals("NoBind")){

//                            startActivity(new Intent(LoginActivity.this,ThirdLoginBindActivity.class));
                            Intent intent = new Intent(LoginActivity.this,ThirdLoginBindActivity.class);
                            intent.putExtra("UnionNo",token);
                            intent.putExtra("Type",String.valueOf(flag));
                            startActivity(intent);
                        }
                    }catch (Exception e){

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
    Handler myHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WEIBO_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1){
                        case 1:
                            Platform pf = ShareSDK.getPlatform(SinaWeibo.NAME);
                            Log.e("LOGIN", "   " + "weibo login done  1");
                            String id = pf.getDb().getUserId();
                            Log.e("LOGIN", "   " + "weibo login  name done  1  "  + id);
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            Log.e("LOGIN", "   " + "weibo login done  2  "+pf.getDb().getUserId());
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            Log.e("LOGIN", "   " + "weibo login done  3  "+pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            Log.e("LOGIN", "   " + "weibo login done  4  "+pf.getDb().getUserName());
                            String sex  = pf.getDb().getUserGender();
                            if(sex == null)
                                myUserInfo.setSex("1");
                            else if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");
                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
//                            Log.e("sharesdk use_icon", pf.getDb().getUserGender());
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().getUserId();
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this,"成功",Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 3;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this,"失败",Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this,"取消",Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };



    public void ThirdWeiboLogin(){
        //初始化新浪平台
        Platform pf = ShareSDK.getPlatform( SinaWeibo.NAME);
//        pf.SSOSetting(true);
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

    Handler myQQHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case QQ_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1){
                        case 1:
                            Platform pf = ShareSDK.getPlatform(QQ.NAME);
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            String sex  = pf.getDb().getUserGender();
                            if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");

                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
                            Log.e("sharesdk use_icon", pf.getDb().getUserGender());
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().getUserId();
//                            token = pf.getDb().getToken();
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this,"成功",Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 2;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this,"q失败",Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this,"q取消",Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };


    public void thirdQQLogin(){
        //初始化qq
        Platform pf = ShareSDK.getPlatform( QQ.NAME);
//        pf.SSOSetting(true);
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


    Handler myWechatHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case WECHAT_MSG_ACTION_CCALLBACK:
                    switch (msg.arg1){
                        case 1:
                            Platform pf = ShareSDK.getPlatform(Wechat.NAME);
                            Log.e("sharesdk use_id", pf.getDb().getUserId()); //获取用户id
                            myUserInfo.setHeadImage(pf.getDb().getUserIcon());
                            myUserInfo.setNickName(pf.getDb().getUserName());
                            String sex  = pf.getDb().getUserGender();
                            if (sex.equals("m"))
                                myUserInfo.setSex("1");
                            else
                                myUserInfo.setSex("2");

                            Log.e("sharesdk use_name", pf.getDb().getUserName());//获取用户名称
                            Log.e("sharesdk use_icon", pf.getDb().getUserIcon());//获取用户头像
                            Log.e("sharesdk use_icon", pf.getDb().getUserGender());
                            Log.e("third 1", msg.obj.toString());
                            token = pf.getDb().getUserId();
//                            token = pf.getDb().getToken();
                            myUserInfo.setUnionNo(token);
                            Toast.makeText(LoginActivity.this,"w成功",Toast.LENGTH_LONG).show();
                            Log.e("third ", msg.obj.toString());
                            flag = 1;
                            mAsyncTaskManager.request(THIRD_LOGIN, true, mydataListener);
                            break;
                        case 2:
                            Toast.makeText(LoginActivity.this,"w失败",Toast.LENGTH_LONG).show();
                            break;
                        case 3:
                            Toast.makeText(LoginActivity.this,"w取消",Toast.LENGTH_LONG).show();
                            break;
                    }
                    break;
            }
        }
    };


    public void thirdWechatLogin(){
        //初始化wechat
        Platform pf = ShareSDK.getPlatform( Wechat.NAME);
//        pf.SSOSetting(true);
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
