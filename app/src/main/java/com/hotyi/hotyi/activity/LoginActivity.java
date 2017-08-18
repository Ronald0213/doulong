package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.wxapi.WXEntryActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.w3c.dom.Text;

public class LoginActivity extends Activity implements View.OnClickListener,OnDataListener {

    public static final int LOGIN = 20;
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
        regToWx();
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
                if (TextUtils.isEmpty(phone_num))
                    Toast.makeText(LoginActivity.this, "手机号码不能为空", Toast.LENGTH_SHORT).show();
                if (TextUtils.isEmpty(password))
                    Toast.makeText(LoginActivity.this,"请输入密码",Toast.LENGTH_LONG);
                mAsyncTaskManager.request(LOGIN,true,this);
                Log.e("LOGIN","CLICK text run");
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
                final SendAuth.Req req = new SendAuth.Req();
                req.scope = "snsapi_userinfo";
                req.state = "diandi_wx_login";
                api.sendReq(req);
                startActivityForResult(new Intent(LoginActivity.this, WXEntryActivity.class), 20);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 20) {

        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        Log.e("LOGIN","CLICK text run  do in background");
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

        Log.e("LOGIN","CLICK text run onSuccess");

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
}
