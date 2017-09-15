package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotyi.hotyi.R;

public class MyAccountActivity extends BaseUiActivity {

    private TextView myAccountPhone ,myAccountSex,myAccountName,myAccountId;
    private LinearLayout myAccountSexClick,myAccountPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        initView();
    }

    public void initView(){
        myAccountId = (TextView)findViewById(R.id.my_account_id);
        myAccountName = (TextView)findViewById(R.id.my_account_name);
        myAccountPhone = (TextView)findViewById(R.id.my_account_phone);
        myAccountSex = (TextView)findViewById(R.id.my_account_sex);
        myAccountSexClick = (LinearLayout)findViewById(R.id.my_account_sex_click);
        myAccountPassword = (LinearLayout)findViewById(R.id.my_account_password);
    }

}
