package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.hotyi.hotyi.R;

public class RegistFinallyActivity extends Activity implements View.OnClickListener {

    private ImageButton imageButton_back;
    private EditText edt_name, edt_sex, edt_password, edt_sure_password;
    private Button btn_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist_finally);
        initView();
        edtListener();
    }

    public void initView() {
        imageButton_back = (ImageButton) findViewById(R.id.regist_finally_imgbtn_back);
        edt_name = (EditText) findViewById(R.id.regist_finally_edt_name);
        edt_sex = (EditText) findViewById(R.id.regist_finally_edt_sex);
        edt_password = (EditText) findViewById(R.id.regist_finally_edt_password);
        edt_sure_password = (EditText) findViewById(R.id.regist_finally_edt_sure_password);
        btn_next = (Button) findViewById(R.id.regist_finally_btn_next);
        imageButton_back.setOnClickListener(this);
        btn_next.setOnClickListener(this);
        edt_sex.setOnClickListener(this);
        btn_next.setEnabled(false);
        btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
    }

    public void edtListener() {
        edt_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = edt_password.getText().toString();
                String password_sure = edt_sure_password.getText().toString();
                int len = edt_password.getText().toString().length();
                if (len < 6) {
                    Intent intent = new Intent(RegistFinallyActivity.this, MyDialogActivity.class);
                    intent.putExtra("regist_password", String.valueOf(len));
                    startActivity(intent);
                } else if (password != null && password_sure != null && password.equals(password_sure)) {
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.login_btn_shape);
                }
            }
        });
        edt_sure_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String password = edt_password.getText().toString();
                String password_sur = edt_sure_password.getText().toString();
                if (password != null && password_sur != null && password.equals(password_sur)) {
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.login_btn_shape);
                }else {
                    btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
                    btn_next.setEnabled(false);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String password = edt_password.getText().toString();
                String password_sur = edt_sure_password.getText().toString();
                if (password != null && password_sur != null && password.equals(password_sur)) {
                    btn_next.setEnabled(true);
                    btn_next.setBackgroundResource(R.drawable.login_btn_shape);
                }else {
                    btn_next.setBackgroundResource(R.drawable.gray_btn_corners);
                    btn_next.setEnabled(false);
                    Intent intent = new Intent(RegistFinallyActivity.this,MyDialogActivity.class);
                    intent.putExtra("passwordSure",false);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist_finally_imgbtn_back:
                finish();
                break;
            case R.id.regist_finally_edt_sex:
                startActivityForResult(new Intent(RegistFinallyActivity.this, MyDialogActivity.class), 1);
                break;
            case R.id.regist_finally_btn_next:
                startActivity(new Intent(RegistFinallyActivity.this, MainActivity.class));
                break;
        }
    }
}
