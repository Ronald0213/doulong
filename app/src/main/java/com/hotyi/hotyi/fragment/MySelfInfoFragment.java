package com.hotyi.hotyi.fragment;


import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.hotyi.hotyi.R;
import com.hotyi.hotyi.activity.AboutActivity;
import com.hotyi.hotyi.activity.FeedBackActivity;
import com.hotyi.hotyi.activity.MyAccountActivity;
import com.hotyi.hotyi.activity.MyActivityActivity;
import com.hotyi.hotyi.activity.MyCodeActivity;
import com.hotyi.hotyi.activity.MyGameActivity;
import com.hotyi.hotyi.activity.MyGameCircleActivity;
import com.hotyi.hotyi.activity.MyProfitActivity;
import com.hotyi.hotyi.activity.SettingActivity;

import io.rong.imageloader.utils.L;

public class MySelfInfoFragment extends Fragment implements View.OnClickListener{

    private TextView mySelfName,mySelfHotyiId;
    private LinearLayout mySelfCode,mySelfAccount,mySelfGame,mySelfProfit,mySelfActivity,mySelfGameCircle,
                            mySelfSetting,mySelfFeedback,mySelfAbout,mySelfPortraitChange;
    private ImageView  mySelfPortrait,mySelfSex;
    private Context mcontext;
    public MySelfInfoFragment(){}

    Handler myAccountHandler =  new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_self_info,container,false);
        initView(view);
//        new MyAccountInfoThread().start();
        return view;
    }

    public void initView(View view){

        mySelfName = (TextView)view.findViewById(R.id.my_self_name);
        mySelfHotyiId = (TextView)view.findViewById(R.id.my_self_hotyi_id);
        mySelfSex = (ImageView)view.findViewById(R.id.my_self_sex);
        mySelfPortrait = (ImageView)view.findViewById(R.id.my_self_portrait);
        mySelfCode = (LinearLayout)view.findViewById(R.id.my_self_code);
        mySelfAccount = (LinearLayout)view.findViewById(R.id.my_self_account);
        mySelfGame = (LinearLayout)view.findViewById(R.id.my_self_game);
        mySelfProfit = (LinearLayout)view.findViewById(R.id.my_self_profit);
        mySelfActivity = (LinearLayout)view.findViewById(R.id.my_self_activity);
        mySelfGame = (LinearLayout)view.findViewById(R.id.my_self_game);
        mySelfGameCircle = (LinearLayout)view.findViewById(R.id.my_self_game_circle);
        mySelfSetting = (LinearLayout)view.findViewById(R.id.my_self_setting);
        mySelfFeedback = (LinearLayout)view.findViewById(R.id.my_self_feedback);
        mySelfAbout = (LinearLayout)view.findViewById(R.id.my_self_about);
        mySelfPortraitChange = (LinearLayout)view.findViewById(R.id.my_self_change_portrait);

        mySelfPortraitChange.setOnClickListener(this);
        mySelfAbout.setOnClickListener(this);
        mySelfFeedback.setOnClickListener(this);
        mySelfSetting.setOnClickListener(this);
        mySelfGameCircle.setOnClickListener(this);
        mySelfGame.setOnClickListener(this);
        mySelfActivity.setOnClickListener(this);
        mySelfProfit.setOnClickListener(this);
        mySelfGame.setOnClickListener(this);
        mySelfAccount.setOnClickListener(this);
        mySelfCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.my_self_code:
                startActivity(new Intent(getContext().getApplicationContext(),MyCodeActivity.class));
                break;
            case R.id.my_self_account:
                startActivity(new Intent(getContext().getApplicationContext(), MyAccountActivity.class));
                break;
            case R.id.my_self_game:
                startActivity(new Intent(getContext().getApplicationContext(), MyGameActivity.class));
                break;
            case R.id.my_self_profit:
                startActivity(new Intent(getContext().getApplicationContext(), MyProfitActivity.class));
                break;
            case R.id.my_self_activity:
                startActivity(new Intent(getContext().getApplicationContext(), MyActivityActivity.class));
                break;
            case R.id.my_self_game_circle:
                startActivity(new Intent(getContext().getApplicationContext(), MyGameCircleActivity.class));
                break;
            case R.id.my_self_setting:
                startActivity(new Intent(getContext().getApplicationContext(), SettingActivity.class));
                break;
            case R.id.my_self_feedback:
                startActivity(new Intent(getContext().getApplicationContext(), FeedBackActivity.class));
                break;
            case R.id.my_self_about:
                startActivity(new Intent(getContext().getApplicationContext(), AboutActivity.class));
                break;
            case R.id.my_self_change_portrait:
//                startActivity(new Intent(getContext().getApplicationContext(),));
                break;
        }
    }

    class MyAccountInfoThread extends Thread{
        @Override
        public void run() {
            super.run();
            Message  msg = new Message();
            msg.what = 1;
            myAccountHandler.sendMessage(msg);
        }
    }
}
