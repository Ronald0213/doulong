package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hotyi.hotyi.R;

public class MyDialogActivity extends Activity {

    private TextView dialog_text;
    private Button left_btn,right_btn;

    private static final int CLOSE = 0;
    public Handler handler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case CLOSE:

                    finish();
                    break;
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.realy_dialog);

        dialog_text = (TextView)findViewById(R.id.dialog_text);
        left_btn = (Button)findViewById(R.id.dialog_left_btn);
        right_btn = (Button)findViewById(R.id.dialog_right_btn);

        Intent intent = getIntent();
        boolean sex_choose = intent.getBooleanExtra("regist",false);
        boolean regist_password = intent.getBooleanExtra("regist_password",false);
        boolean regist_password_sure = intent.getBooleanExtra("regist_password_sure",false);
        boolean regist_succeed = intent.getBooleanExtra("regist_succeed",false);
        if (sex_choose){
            dialog_text.setText("请选择您的性别");
            left_btn.setText("男");
            right_btn.setText("女");
            sexClick();
        }else if (regist_password){
            dialog_text.setText("请输入不少于6位数的密码");
            new TimeThread().start();
        }else if (regist_password_sure){
            dialog_text.setText("密码确认错误，请重新输入");
            new TimeThread().start();
        }else if (regist_succeed){
            dialog_text.setText("恭喜，注册成功！");
            setResult(5);
            new TimeThread().start();
        }

    }

    /**
     *
     * 3秒后自动关闭
     *
     * */
    class TimeThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                sleep(1500);
            }catch (Exception e){

            }
            Message msg =  new Message();
            msg.what = CLOSE;
            handler.sendMessage(msg);
        }
    }

    public void sexClick(){
        left_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("SEX","男");
                setResult(1,intent);
                finish();
            }
        });
        right_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("SEX","女");
                setResult(1,intent);
                finish();
            }
        });
    }
}
