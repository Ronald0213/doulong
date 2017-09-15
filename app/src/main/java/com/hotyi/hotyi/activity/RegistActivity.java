package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;

public class RegistActivity extends BaseUiActivity implements View.OnClickListener{

    private ImageView imageButton_back;
    private EditText editText_phone;
    private Button button_next;
    public AsyncTaskManager mAsyncTaskManager;
    private String phone_num;



    public static final int PHONE_REGIST = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());
        initView();
    }
    public void initView(){
        imageButton_back = (ImageView)findViewById(R.id.regist_btn_back);
        editText_phone = (EditText)findViewById(R.id.regist_edit_phone);
        button_next = (Button)findViewById(R.id.regist_btn_next);

        imageButton_back.setOnClickListener(this);
        button_next.setOnClickListener(this);
//        button_next.setBackgroundResource(R.drawable.gray_btn_corners);

//        editText_phone.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                int len = editText_phone.getText().toString().length();
//                if (len == 11){
//                    button_next.setBackgroundResource(R.drawable.login_btn_shape);
//                }else {
//                    button_next.setBackgroundResource(R.drawable.gray_btn_corners);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.regist_btn_back:
                setResult(20);
                finish();
                break;
            case R.id.regist_btn_next:
                String phone_num = editText_phone.getText().toString();
                int len = phone_num.length();
                if(len < 11){
                    Toast.makeText(RegistActivity.this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Intent intent = new Intent(RegistActivity.this, RegistNextActivity.class);
                    intent.putExtra("phone", phone_num);
                    startActivityForResult(intent, 25);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25){

        }
    }

//    @Override
//    public Object doInBackground(int requestCode, String parameter) throws HttpException {
//        if (requestCode == PHONE_REGIST){
//            try {
//
//                String login_url = MyAsynctask.HOST + MyAsynctask.Login;
//                URL url = new URL(login_url);
//                HashMap<String, String> map = new HashMap<>();
//                map.put("PhoneNum", phone_num);
//                if(phone_num == null){
//                    Log.e("RSA",".......");
//                }
//                else
//                    Log.e("RSA","----   ");
//                Log.e("RSA","++++   "+phone_num);
//                return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
////                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
//            } catch (Exception e) {
//
//            }
//
//        }
//        return super.doInBackground(requestCode, parameter);
//
//    }
//
//    @Override
//    public void onSuccess(int requestCode, Object result) {
//        super.onSuccess(requestCode, result);
//        if (requestCode == PHONE_REGIST){
//            try {
//                String str = result.toString();
//                JSONObject jsonObject = new JSONObject(str);
//                final PhoneRegistInfo phoneRegistInfo = new PhoneRegistInfo();
//                phoneRegistInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
//                phoneRegistInfo.setResult_msg(jsonObject.get("result_msg").toString());
//                phoneRegistInfo.setReturn_msg(jsonObject.get("retrun_msg").toString());
//                if (phoneRegistInfo.getCode() == 1) {
//                    Intent intent = new Intent(RegistActivity.this, RegistNextActivity.class);
//                    intent.putExtra("phone", phone_num);
//                    startActivityForResult(intent, 25);
//                }if (phoneRegistInfo.getCode() == 2){
//                    Toast.makeText(getApplicationContext(),phoneRegistInfo.getResult_msg(),Toast.LENGTH_SHORT).show();
//                }
//            }catch (Exception e){
//                Log.e("LOGIN","  " +e.toString());
//            }
//        }
//
//    }
//
//    @Override
//    public void onFailure(int requestCode, int state, Object result) {
//        super.onFailure(requestCode, state, result);
//    }
}
