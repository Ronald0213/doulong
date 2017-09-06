package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.LoginInfo;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

public class ThirdLoginBindActivity extends Activity implements OnDataListener, View.OnClickListener{

    private ImageButton backImgBtn;
    private EditText phoneEdit,codeEdit;
    private TextView getCodeText;
    private Button nextBtn;
    private AsyncTaskManager asyncTaskManager;
    private static final int GET_CODE = 27;
    private static final int THIRD_REGIST = 28;

    private String unionNo;
    private String type;
    private MyUserInfo myUserInfo = MyUserInfo.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_login_bind);

        Intent intent = getIntent();
        unionNo = intent.getStringExtra("UnionNo");
        type = intent.getStringExtra("Type");
        Log.e("third",type);
        backImgBtn = (ImageButton)findViewById(R.id.third_bind_btn_back);
        phoneEdit = (EditText)findViewById(R.id.third_bind_edit_phone);
        codeEdit = (EditText)findViewById(R.id.third_bind_edt_code);
        getCodeText = (TextView)findViewById(R.id.third_bind_txt_getcode);
        nextBtn = (Button)findViewById(R.id.third_bind_btn_next);
        backImgBtn.setOnClickListener(this);
        getCodeText.setOnClickListener(this);
        nextBtn.setOnClickListener(this);
        asyncTaskManager = AsyncTaskManager.getInstance(ThirdLoginBindActivity.this);
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_CODE:
                try{
                    String phone_num = phoneEdit.getText().toString();
                    String login_url = MyAsynctask.HOST + MyAsynctask.BindCode;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("PhoneNum", phone_num);
                    if(phone_num == null){
                        Log.e("RSA",".......");
                    }
                    else
                        Log.e("RSA","----   "+phone_num.toString());
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);

                }catch (Exception e){

                }

                break;
            case THIRD_REGIST:
                try {
                    String phone_num = phoneEdit.getText().toString();
                    String login_url = MyAsynctask.HOST + MyAsynctask.ThirdReg;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();

                    map.put("Mobile", phone_num);
                    Log.e("third", "  phone");
                    map.put("NickName",myUserInfo.getNickName());
                    Log.e("third", "  name");
                    map.put("Gender",myUserInfo.getSex());
                    Log.e("third", "  sex  " + myUserInfo.getSex());
                    map.put("HeadImage",myUserInfo.getHeadImage());
                    Log.e("third", "  head  "  + myUserInfo.getHeadImage());
                    map.put("UnionNo",unionNo);
                    Log.e("third", "  unionNo  "+ unionNo);
                    map.put("Type",type);
                    Log.e("third", "  type  "+type);
                    map.put("PhoneCode",codeEdit.getText().toString());
                    Log.e("third", "  code   " +codeEdit.getText().toString());

                    if(phone_num == null){
                        Log.e("RSA",".......");
                    }
                    else
                        Log.e("RSA","----   "+phone_num.toString());
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);

                }catch (Exception e ){

                }
                break;
        }


        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

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
                        Toast.makeText(ThirdLoginBindActivity.this,myLoginInfo.getResult_msg(),Toast.LENGTH_SHORT).show();
                    }else if (myLoginInfo.getCode() == 2){
                        Toast.makeText(ThirdLoginBindActivity.this,myLoginInfo.getResult_msg(),Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e){
                    Log.e("LOGIN","  " +e.toString());
                }
                break;
            case THIRD_REGIST:
                try {
                    String str = result.toString();
                    Log.e("regist", "111111  finally   " + str);
                    JSONObject jsonObject = new JSONObject(str);
                    final LoginInfo myLoginInfo = new LoginInfo();
                    myLoginInfo.setCode(Integer.valueOf(jsonObject.get("code").toString()));
                    if (myLoginInfo.getCode() == 1) {
                        Intent intent = new Intent(ThirdLoginBindActivity.this,MyDialogActivity.class);
                        intent.putExtra("regist_succeed",true);
                        startActivityForResult(intent,5);
                    } else if (myLoginInfo.getCode() == 2) {
                        Toast.makeText(ThirdLoginBindActivity.this, jsonObject.get("result_msg").toString(), Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    Log.e("regist", " finally " + e.toString());
                }
                break;
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.third_bind_txt_getcode:
                if (phoneEdit.getText().toString().length() < 11)
                    Toast.makeText(ThirdLoginBindActivity.this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                else
                    asyncTaskManager.request(GET_CODE,true,this);
                break;
            case R.id.third_bind_btn_next:
                if (codeEdit.getText().toString().length() < 6)
                    Toast.makeText(ThirdLoginBindActivity.this,"请输入正确的手机号码",Toast.LENGTH_SHORT).show();
                else
                    asyncTaskManager.request(THIRD_REGIST,true,this);
                break;
            case R.id.third_bind_btn_back:
                finish();
                break;
        }
    }
}
