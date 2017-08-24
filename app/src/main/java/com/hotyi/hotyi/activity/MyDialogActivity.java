package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.hotyi.hotyi.R;

public class MyDialogActivity extends Activity {

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
        new TimeThread().start();
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
                sleep(3000);
            }catch (Exception e){

            }
            Message msg =  new Message();
            msg.what = CLOSE;
            handler.sendMessage(msg);
        }
    }
}
