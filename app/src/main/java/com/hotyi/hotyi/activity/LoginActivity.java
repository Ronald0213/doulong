package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hotyi.hotyi.R;

import org.w3c.dom.Text;

public class LoginActivity extends Activity implements View.OnClickListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                break;
            case R.id.text_forget_password:
              //  startActivity(new Intent(LoginActivity.this,Forg));
                break;
            case R.id.text_regist:
                startActivityForResult(new Intent(LoginActivity.this,RegistActivity.class),20);
                break;
            case R.id.imgbtn_qq:
                break;
            case R.id.imgbtn_weibo:
                break;
            case R.id.imgbtn_weixin:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
