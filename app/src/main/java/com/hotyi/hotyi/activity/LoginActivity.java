package com.hotyi.hotyi.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.json.JSONObject;

import java.net.URL;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends MyBaseActivity implements View.OnClickListener, OnDataListener {

    private MyUserInfo myUserInfo;
    private AlertDialog dialog ;
    private SharedPreferences sharedPreferences;
    private static final int MAX_ENCRYPT_BLOCK = 1024;
    private StringBuffer stringBuffer;
    public static final int LOGIN = 20;
    public static final int GET_TOKEN = 21;
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
                break;
            case R.id.imgbtn_weibo:
                break;
            case R.id.imgbtn_weixin:
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

    /**
     *    RSA方法已封装入RSAUtil
     *
     *
     *
     *   RSA 加密 解密
     * */

    /**
     * 用公钥对字符串进行加密
     */
//    private  String encryptByPublicKey(String str) throws Exception {
//        final String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgxOFIHXabcBxZOFBWz55WI18tQDTfHrU2IeJz6MVMgVv3yPXorrc8XucjPr16uMPy77qFGpJ2exOXpbzL0Rrmq9KzMDBdEWMttcppKVi+oTbz3xRGONyq3Gi22fNOiRPOOWO5NeuYMNNo7iV3egNgt2kHsW86/XAwB9Cbr0GUyQIDAQAB";
//        try {
//            byte[] publicKey = Base64.decode(public_key,Base64.DEFAULT);
//            byte[] data = str.getBytes();
//             得到公钥
//            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
//            KeyFactory kf = KeyFactory.getInstance(RSA);
//            PublicKey keyPublic = kf.generatePublic(keySpec);
//             加密数据
//            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
//            cp.init(Cipher.ENCRYPT_MODE, keyPublic);
//            String str_ss = Base64.encodeToString(cp.doFinal(data),Base64.DEFAULT);
//            return str_ss;
//        } catch (Exception e) {
//            Log.e("RSA", e.toString());
//            return null;
//        }
//
//    }

    /**
     * 使用私钥进行解密
     */
//    private  String decryptByPrivateKey(String str) throws Exception {
//        String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAODE4UgddptwHFk4UFbPnlYjXy1ANN8etTYh4nPoxUyBW/fI9eiutzxe5yM+vXq4w/LvuoUaknZ7E5elvMvRGuar0rMwMF0RYy21ymkpWL6hNvPfFEY43KrcaLbZ806JE845Y7k165gw02juJXd6A2C3aQexbzr9cDAH0JuvQZTJAgMBAAECgYEAl9nrKUFehBz1ygEVpdCWdDNpdbTPA35Hhs7VouE7ijhK3dhS6mQ/PvYOyez1Lhftqg7zwED3ejwkPGuoZTpcJP62hauoZooR8XCAJ6ZHEZpuJEwUH6DUiujmitkXzXXkZ+DhFEvWzLY4AfiuOLv/Az+MHVHbofAl4F9BI0fmr0ECQQD62BAzNU2ePqYLzodLjjM4y1ndH/XrMhdeo7nhUY47vHOl5uxiE+KvXuuZ29kUH7hMsG/nUiovPRsOT88kzTatAkEA5WOcYlxA0CLeGd3E62u4zH7CNrquZ3+423OCyjjx7JxPa9Fq2nxIPqiAp9i/Y2qFBErY4egI/VWzdj61aDzGDQJAShu/XYGn9tKHeAGCUz4lv+fEGuIwY1YfNWSlq/3OSbO5bxA0Uh2R4UHn1ULwdVORvYZ66RqLP/2LmsTVbAf82QJBALSX86rMjopOqSUcH8hoipkUwrprxprdRyAelL24j16EwVJVERbp+ca6ym9aiXMvjYGPm6hfEZTBQAS74f4qupECQQD6Qq+wmcDB/qGtNIFUTCMj5L1ozZujmP6w1TmyLnlJqIkNbVp5QCXIvb7dSZA4LG7IvwkcP++49C66IHjIMKWf";
//        byte[] privateKey = Base64.decode(private_key,Base64.DEFAULT);
        // 得到私钥
//        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
//        KeyFactory kf = KeyFactory.getInstance(RSA);
//        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
//        byte[] encrypted = str.getBytes();
        // 解密数据
//        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
//        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
//        byte[] arr = cp.doFinal(encrypted);
//        String str_ss = Base64.encodeToString(cp.doFinal(arr),Base64.DEFAULT);
//        return str_ss;
//    }

    @Override
    protected void onStart() {
        super.onStart();
//        startActivity(new Intent(LoginActivity.this,MyDialogActivity.class));

    }

}
