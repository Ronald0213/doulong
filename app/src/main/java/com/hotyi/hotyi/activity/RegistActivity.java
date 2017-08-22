package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.hotyi.hotyi.R;

public class RegistActivity extends Activity implements View.OnClickListener{

    private ImageButton imageButton_back;
    private EditText editText_phone;
    private Button button_next;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        initView();
    }
    public void initView(){
        imageButton_back = (ImageButton)findViewById(R.id.regist_btn_back);
        editText_phone = (EditText)findViewById(R.id.regist_edit_phone);
        button_next = (Button)findViewById(R.id.regist_btn_next);

        imageButton_back.setOnClickListener(this);
        button_next.setOnClickListener(this);

        editText_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


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
                Intent intent =  new Intent(RegistActivity.this,RegistNextActivity.class);
                intent.putExtra("phone",phone_num);
                startActivityForResult(intent,25);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 25){

        }
    }
}
