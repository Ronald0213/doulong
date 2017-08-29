package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.LoginData;
import com.hotyi.hotyi.other.hotyiClass.LoginInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class RegistNextActivity extends MyBaseActivity implements OnDataListener, View.OnClickListener {

    private ImageButton imgbtn_back;
    private EditText edtbtn_code;
    private TextView txt_getCode;
    private Button btn_next;
    public AsyncTaskManager mAsyncTaskManager;

    private static final int GET_CODE = 25;



    private String phone_num = null;
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
        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());
        initView();
        Intent intent = getIntent();
        phone_num = intent.getStringExtra("phone");
    }

    public void initView(){
        imgbtn_back = (ImageButton)findViewById(R.id.regist_next_imgbtn_back);
        edtbtn_code = (EditText)findViewById(R.id.regist_next_edt_code);
        txt_getCode = (TextView)findViewById(R.id.regist_next_txt_getcode);
        btn_next = (Button)findViewById(R.id.regist_next_btn_next);

        imgbtn_back.setOnClickListener(this);
        txt_getCode.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
        btn_next.setEnabled(false);
        edtbtn_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int len = edtbtn_code.getText().toString().length();
                if(len == 6){
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.login_btn_shape);
                }else {
                    btn_next.setEnabled(false);
                    btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }


    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_CODE:
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
            case GET_CODE:
                try {
                    String str = result.toString();
                    Log.e("regist","111111     "+str);
                    JSONObject jsonObject = new JSONObject(str);
                    final LoginInfo myLoginInfo = new LoginInfo();
                    myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
                    myLoginInfo.setResult_msg(jsonObject.get("result_msg").toString());
                    myLoginInfo.setReturn_msg(jsonObject.get("retrun_msg").toString());
                    if (myLoginInfo.getCode() == 1) {
                        Toast.makeText(RegistNextActivity.this,myLoginInfo.getResult_msg(),Toast.LENGTH_SHORT).show();
                    }else if (myLoginInfo.getCode() == 2){
                        Toast.makeText(RegistNextActivity.this,myLoginInfo.getResult_msg(),Toast.LENGTH_SHORT).show();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regist_next_txt_getcode:
                    mAsyncTaskManager.request(GET_CODE,true,this);
                break;
            case R.id.regist_next_imgbtn_back:
                finish();
                break;
            case R.id.regist_next_btn_next:
                String code = edtbtn_code.getText().toString();
                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.JudgeCode;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    if(phone_num == null){
                        Log.e("RSA",".......");
                    }
                    else{
                        map.put("PhoneNum",phone_num);
                        map.put("VCode",code);
                        map.put("UserWay","1");
                    }
                    Log.e("RSA","----   "+phone_num.toString());
                   String result =  HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    JSONObject jsonObject = new JSONObject(result);

                    int result_code = Integer.valueOf(jsonObject.getString("code").toString());
                    String result_msg = jsonObject.get("result_msg").toString();
                    String return_msg = jsonObject.get("retrun_msg").toString();
                    if(result_code == 1){
                        startActivity(new Intent(RegistNextActivity.this,RegistFinallyActivity.class));
                    }else {
                        Toast.makeText(RegistNextActivity.this,result_msg,Toast.LENGTH_SHORT).show();
                    }

                } catch (Exception e) {

                }
                break;
        }
    }
}
