package com.hotyi.hotyi.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.LoginInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class RegistFinallyActivity extends MyBaseActivity implements View.OnClickListener, View.OnFocusChangeListener {

    private ImageView imageButton_back;
    private EditText edt_name, edt_sex, edt_password;
    private Button btn_next,btn_nan,btn_nv;
    private AsyncTaskManager asyncTaskManager;
    private Context mcontext;
//    public String android_id;
    private StringBuffer stringBuffer;
    private String phoneNum;
    private String code;
//    private boolean password_input_again = false;

    private static final int REGIST = 45;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_finally);
//        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        mcontext = this;
        stringBuffer = new StringBuffer();
        asyncTaskManager = AsyncTaskManager.getInstance(mcontext);
        Intent intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        code = intent.getStringExtra("code");
        initView();
        edtListener();
    }

    public void initView() {
        imageButton_back = (ImageView) findViewById(R.id.regist_finally_imgbtn_back);
        edt_name = (EditText) findViewById(R.id.regist_finally_edt_name);
        edt_sex = (EditText) findViewById(R.id.regist_finally_edt_sex);
        edt_password = (EditText) findViewById(R.id.regist_finally_edt_password);
//        edt_sure_password = (EditText) findViewById(R.id.regist_finally_edt_sure_password);
        btn_next = (Button) findViewById(R.id.regist_finally_btn_next);
        btn_nan = (Button)findViewById(R.id.regist_finally_nan);
        btn_nv = (Button)findViewById(R.id.regist_finally_nv);
        btn_nan.setOnClickListener(this);
        btn_nv.setOnClickListener(this);
        imageButton_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        edt_sex.setOnClickListener(this);
        btn_next.setEnabled(false);
        btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
    }

    public void edtListener() {
//        edt_sex.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                if (b) {
//                    Intent intent = new Intent(RegistFinallyActivity.this, MyDialogActivity.class);
//                    intent.putExtra("regist", true);
//                    startActivityForResult(intent, 1);
//                }
//            }
//        });

        edt_password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b) {
                    String password = edt_password.getText().toString();
//                    String password_sure = edt_sure_password.getText().toString();
                    int len = edt_password.getText().toString().length();
                    if (len < 6) {
                        Intent intent = new Intent(RegistFinallyActivity.this, MyDialogActivity.class);
                        intent.putExtra("regist_password", true);
                        startActivity(intent);
                        btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
                        btn_next.setEnabled(false);
                    } else  {
                        btn_next.setEnabled(true);
                        btn_next.setBackgroundResource(R.drawable.login_btn_shape);
                    }
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist_finally_nan:
                btn_nan.setBackgroundResource(R.drawable.blue_coners_btn);
                btn_nan.setTextColor(getResources().getColor(R.color.darkblue));
                edt_sex.setText("男");
                btn_nv.setBackgroundResource(R.drawable.gray_corners_btn);
                btn_nv.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.regist_finally_nv:
                btn_nv.setBackgroundResource(R.drawable.blue_coners_btn);
                btn_nv.setTextColor(getResources().getColor(R.color.darkblue));
                edt_sex.setText("女");
                btn_nan.setBackgroundResource(R.drawable.gray_corners_btn);
                btn_nan.setTextColor(getResources().getColor(R.color.gray));
                break;
            case R.id.regist_finally_imgbtn_back:
                finish();
                break;
//            case R.id.regist_finally_edt_sex:
//                Intent intent = new Intent(RegistFinallyActivity.this, MyDialogActivity.class);
//                intent.putExtra("regist",true);
//                startActivityForResult(intent, 1);
//                break;
            case R.id.regist_finally_btn_next:
//                if (! edt_sure_password.getText().toString().equals(edt_password.getText().toString())){
//                    Intent intent = new Intent(RegistFinallyActivity.this, MyDialogActivity.class);
//                    intent.putExtra("regist_password_sure", true);
//                    startActivity(intent);
//                    return;
//                }
                String sex = "0";
                if (edt_sex.getText().toString().equals("男")){
                    sex = "1";
                }else if (edt_sex.getText().toString().equals("女")){
                    sex = "2";
                }else {
                    return;
                }
                Log.e("sex", sex);

                stringBuffer = new StringBuffer();

                stringBuffer.append("APP").append("&").append("Android").append("&")
                        .append(phoneNum).append("&").append(edt_password.getText().toString()).append("&").append(sex)
                        .append("&").append(edt_name.getText().toString()).append("&").append(System.currentTimeMillis());
                asyncTaskManager.request(REGIST, true, this);
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case REGIST:
                try {
                    String RSAText = RSAUtil.encryptByPublicKey(stringBuffer.toString());
                    String login_url = MyAsynctask.HOST + MyAsynctask.Regist;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    if (phoneNum == null) {
                        Log.e("RSA", ".......");
                    } else {
                        map.put("PhoneCode", code.toString());
                        map.put("RSAText", RSAText);
                        Log.e("regist", phoneNum + "  " + code);

                    }
                    Log.e("RSA", "----   " + phoneNum.toString());
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);

                } catch (Exception e) {
                    Log.e("regist", e.toString());
                }


                break;
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case REGIST:
                try {
                    String str = result.toString();
                    Log.e("regist", "111111  finally   " + str);
                    JSONObject jsonObject = new JSONObject(str);
                    final LoginInfo myLoginInfo = new LoginInfo();
                    myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
                    if (myLoginInfo.getCode() == 1) {
                        Intent intent = new Intent(RegistFinallyActivity.this,MyDialogActivity.class);
                        intent.putExtra("regist_succeed",true);
                        startActivityForResult(intent,5);
                    } else if (myLoginInfo.getCode() == 2) {
                        Toast.makeText(RegistFinallyActivity.this, jsonObject.get("result_msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("regist", " finally " + e.toString());
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            String sex = data.getStringExtra("SEX");
            if (sex != null)
                edt_sex.setText(sex);
        }else if(requestCode == 5){
            startActivity(new Intent(RegistFinallyActivity.this,LoginActivity.class));
        }
    }

    @Override
    public void onFocusChange(View view, boolean b) {

    }
}
