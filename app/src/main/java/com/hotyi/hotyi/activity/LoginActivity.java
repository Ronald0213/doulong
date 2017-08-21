package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.HotyiClass.LoginData;
import com.hotyi.hotyi.other.HotyiClass.LoginInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.JsonMananger;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.async.AsyncResult;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.w3c.dom.Text;

import java.math.BigInteger;
import java.net.URL;
import java.security.Key;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class LoginActivity extends MyBaseActivity implements View.OnClickListener, OnDataListener {


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
    /**
     * RSA加密
     */
    public static final String RSA = "RSA";// 非对称加密密钥算法
    public static final String ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";//加密填充方式
    public static final int DEFAULT_KEY_SIZE = 2048;//秘钥默认长度
    public static final byte[] DEFAULT_SPLIT = "#PART#".getBytes();// 当要加密的内容超过bufferSize，则采用partSplit进行分块加密
    public static final int DEFAULT_BUFFERSIZE = (DEFAULT_KEY_SIZE / 8) - 11;// 当前秘钥支持加密的最大字节数
//    private  String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgxOFIHXabcBxZOFBWz55WI18tQDTfHrU2IeJz6MVMgVv3yPXorrc8XucjPr16uMPy77qFGpJ2exOXpbzL0Rrmq9KzMDBdEWMttcppKVi+oTbz3xRGONyq3Gi22fNOiRPOOWO5NeuYMNNo7iV3egNgt2kHsW86/XAwB9Cbr0GUyQIDAQAB";
//    private  String private_key ="MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAODE4UgddptwHFk4UFbPnlYjXy1ANN8etTYh4nPoxUyBW/fI9eiutzxe5yM+vXq4w/LvuoUaknZ7E5elvMvRGuar0rMwMF0RYy21ymkpWL6hNvPfFEY43KrcaLbZ806JE845Y7k165gw02juJXd6A2C3aQexbzr9cDAH0JuvQZTJAgMBAAECgYEAl9nrKUFehBz1ygEVpdCWdDNpdbTPA35Hhs7VouE7ijhK3dhS6mQ/PvYOyez1Lhftqg7zwED3ejwkPGuoZTpcJP62hauoZooR8XCAJ6ZHEZpuJEwUH6DUiujmitkXzXXkZ+DhFEvWzLY4AfiuOLv/Az+MHVHbofAl4F9BI0fmr0ECQQD62BAzNU2ePqYLzodLjjM4y1ndH/XrMhdeo7nhUY47vHOl5uxiE+KvXuuZ29kUH7hMsG/nUiovPRsOT88kzTatAkEA5WOcYlxA0CLeGd3E62u4zH7CNrquZ3+423OCyjjx7JxPa9Fq2nxIPqiAp9i/Y2qFBErY4egI/VWzdj61aDzGDQJAShu/XYGn9tKHeAGCUz4lv+fEGuIwY1YfNWSlq/3OSbO5bxA0Uh2R4UHn1ULwdVORvYZ66RqLP/2LmsTVbAf82QJBALSX86rMjopOqSUcH8hoipkUwrprxprdRyAelL24j16EwVJVERbp+ca6ym9aiXMvjYGPm6hfEZTBQAS74f4qupECQQD6Qq+wmcDB/qGtNIFUTCMj5L1ozZujmP6w1TmyLnlJqIkNbVp5QCXIvb7dSZA4LG7IvwkcP++49C66IHjIMKWf";


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
        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());
        initView();
        stringBuffer = new StringBuffer();
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
                    stringBuffer.append(android_id).append("&").append(phone_num).append("&").append(encryptByPublicKey(password)).append(System.currentTimeMillis());
                }catch (Exception e){
                    Toast.makeText(LoginActivity.this,e.toString(),Toast.LENGTH_SHORT).show();
                    Log.e("RSA",e.toString());
                    break;
                }
                mAsyncTaskManager.request(LOGIN, true, this);
                Log.e("LOGIN", "CLICK text run");
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
        Log.e("LOGIN", "CLICK text run  do in background");
        switch (requestCode) {
            case LOGIN:

                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.Login;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RSAText", stringBuffer.toString());
                    return HotyiHttpConnection.getInstance(getApplicationContext()).postConnection(map, url);
                } catch (Exception e) {

                }


                break;
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

        Log.e("LOGIN", "CLICK text run onSuccess");
        if (result != null) {
            switch (requestCode) {
                case LOGIN:
                    JSONObject jsonObject = JSON.parseObject(result.toString());
                    final LoginInfo myLoginInfo = new LoginInfo();
                    myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
                    myLoginInfo.setResult_msg(jsonObject.get("result_msg").toString());
                    myLoginInfo.setReturn_msg(jsonObject.get("return_msg").toString());
                    JSONObject logindataJson = JSON.parseObject(jsonObject.get("data").toString());
                    LoginData loginData = new LoginData();
                    loginData.setRYToken(logindataJson.get("RYToken").toString());
                    loginData.setNickName(logindataJson.get("NickName").toString());
                    loginData.setHeadImageUrl(logindataJson.get("HeadImageUrl").toString());
                    loginData.setAccountInfo(logindataJson.get("AccountInformation").toString());
                    if (myLoginInfo.getCode() == 1) {
                        String token = myLoginInfo.getLoginData().getRYToken();
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
                    }
                    break;
                case GET_TOKEN:
                    break;
            }
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    /**
     *
     *   RSA 加密 解密
     * */

    /**
     * 用公钥对字符串进行加密
     */
    private  String encryptByPublicKey(String str) throws Exception {
        String public_key = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDgxOFIHXabcBxZOFBWz55WI18tQDTfHrU2IeJz6MVMgVv3yPXorrc8XucjPr16uMPy77qFGpJ2exOXpbzL0Rrmq9KzMDBdEWMttcppKVi+oTbz3xRGONyq3Gi22fNOiRPOOWO5NeuYMNNo7iV3egNgt2kHsW86/XAwB9Cbr0GUyQIDAQAB";
        try {
            byte[] publicKey = Base64.decode(public_key, Base64.DEFAULT);
            Log.e("RSA", "公钥加密方法+++++++++1");
            byte[] data = str.getBytes();
            Log.e("RSA", "公钥加密方法+++++++++2");
            // 得到公钥
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            Log.e("RSA", "公钥加密方法+++++++++3");
            KeyFactory kf = KeyFactory.getInstance(RSA, "BC");
            Log.e("RSA", "公钥加密方法+++++++++4");
            PublicKey keyPublic = kf.generatePublic(keySpec);
            Log.e("RSA", "公钥加密方法+++++++++5");
            // 加密数据
            Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
            Log.e("RSA", "公钥加密方法+++++++++6");
            cp.init(Cipher.ENCRYPT_MODE, keyPublic);
            Log.e("RSA", "公钥加密方法+++++++++7");
            return cp.doFinal(data).toString();
        } catch (Exception e) {
            Log.e("RSA", e.toString());
            return null;
        }

    }

    /**
     * 使用私钥进行解密
     */
    private  String decryptByPrivateKey(String str) throws Exception {
        String private_key = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAODE4UgddptwHFk4UFbPnlYjXy1ANN8etTYh4nPoxUyBW/fI9eiutzxe5yM+vXq4w/LvuoUaknZ7E5elvMvRGuar0rMwMF0RYy21ymkpWL6hNvPfFEY43KrcaLbZ806JE845Y7k165gw02juJXd6A2C3aQexbzr9cDAH0JuvQZTJAgMBAAECgYEAl9nrKUFehBz1ygEVpdCWdDNpdbTPA35Hhs7VouE7ijhK3dhS6mQ/PvYOyez1Lhftqg7zwED3ejwkPGuoZTpcJP62hauoZooR8XCAJ6ZHEZpuJEwUH6DUiujmitkXzXXkZ+DhFEvWzLY4AfiuOLv/Az+MHVHbofAl4F9BI0fmr0ECQQD62BAzNU2ePqYLzodLjjM4y1ndH/XrMhdeo7nhUY47vHOl5uxiE+KvXuuZ29kUH7hMsG/nUiovPRsOT88kzTatAkEA5WOcYlxA0CLeGd3E62u4zH7CNrquZ3+423OCyjjx7JxPa9Fq2nxIPqiAp9i/Y2qFBErY4egI/VWzdj61aDzGDQJAShu/XYGn9tKHeAGCUz4lv+fEGuIwY1YfNWSlq/3OSbO5bxA0Uh2R4UHn1ULwdVORvYZ66RqLP/2LmsTVbAf82QJBALSX86rMjopOqSUcH8hoipkUwrprxprdRyAelL24j16EwVJVERbp+ca6ym9aiXMvjYGPm6hfEZTBQAS74f4qupECQQD6Qq+wmcDB/qGtNIFUTCMj5L1ozZujmP6w1TmyLnlJqIkNbVp5QCXIvb7dSZA4LG7IvwkcP++49C66IHjIMKWf";
        byte[] privateKey = private_key.getBytes();
        // 得到私钥
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory kf = KeyFactory.getInstance(RSA);
        PrivateKey keyPrivate = kf.generatePrivate(keySpec);
        byte[] encrypted = str.getBytes();
        // 解密数据
        Cipher cp = Cipher.getInstance(ECB_PKCS1_PADDING);
        cp.init(Cipher.DECRYPT_MODE, keyPrivate);
        byte[] arr = cp.doFinal(encrypted);
        return arr.toString();
    }


}
