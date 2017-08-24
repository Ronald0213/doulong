package com.hotyi.hotyi.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.broadcast.BroadcastManager;
import com.hotyi.hotyi.fragment.ContactsFragment;
import com.hotyi.hotyi.fragment.FindFragment;
import com.hotyi.hotyi.fragment.GameFragment;
import com.hotyi.hotyi.fragment.MySelfInfoFragment;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.adpter.ConversationListAdapterEx;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.manager.IUnReadMessageObserver;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.message.ContactNotificationMessage;

public class MainActivity extends FragmentActivity implements View.OnClickListener,OnDataListener,ViewPager.OnPageChangeListener,IUnReadMessageObserver {

    private static final int GET_INFO = 30;
    public static ViewPager myViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private Dialog dialog;
    public AsyncTaskManager mAsyncTaskManager;
    private ConversationListFragment mConversationListFragment = null;
    private boolean isDebug;
    private Context mContext;
    private Conversation.ConversationType[] mConversationsTypes = null;
    private LinearLayout main_msg,main_contect,main_game,main_find,main_mine;
    private String key_str = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        mAsyncTaskManager = AsyncTaskManager.getInstance(mContext);
        initView();
    }

    void initView(){
        myViewPager = (ViewPager)findViewById(R.id.main_viewpager);
        main_msg = (LinearLayout)findViewById(R.id.main_msg);
        main_game = (LinearLayout)findViewById(R.id.main_game);
        main_contect = (LinearLayout)findViewById(R.id.main_contect);
        main_find = (LinearLayout)findViewById(R.id.main_find);
        main_mine = (LinearLayout)findViewById(R.id.main_mine);
        myViewPager.setOnPageChangeListener(this);
        main_msg.setOnClickListener(this);
        main_game.setOnClickListener(this);
        main_mine.setOnClickListener(this);
        main_contect.setOnClickListener(this);
        main_find.setOnClickListener(this);

        Fragment conversationList = initConversationList();
        mFragmentList.add(conversationList);
        mFragmentList.add(new ContactsFragment());
        mFragmentList.add(new GameFragment());
        mFragmentList.add(new FindFragment());
        mFragmentList.add(new MySelfInfoFragment());
        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }
        };
        myViewPager.setAdapter(fragmentPagerAdapter);
        myViewPager.setOffscreenPageLimit(5);
        myViewPager.setOnPageChangeListener(this);
        BroadcastManager.getInstance(mContext).addAction("NEW_MESSAGE", new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
//                mMineRed.setVisibility(View.VISIBLE);
            }
        });
        initData();
        mAsyncTaskManager.request(GET_INFO, true, this);


    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_INFO:

                try {

                    String login_url = MyAsynctask.HOST + MyAsynctask.Login;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("RSAText", key_str);
                    if(key_str == null){
                        Log.e("RSA",".......");
                    }
                    else
                        Log.e("RSA","----   "+key_str.toString());
                    Log.e("RSA","++++   "+key_str);
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

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    class InfoThread extends Thread{
        @Override
        public void run() {
            super.run();

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.main_msg:
                break;
            case R.id.main_contect:
                break;
            case R.id.main_game:
                break;
            case R.id.main_find:
                break;
            case R.id.main_mine:
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            if (isDebug) {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "true") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "true")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .appendQueryParameter(Conversation.ConversationType.DISCUSSION.getName(), "true")
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM,
                        Conversation.ConversationType.DISCUSSION
                };

            } else {
                uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                        .appendPath("conversationlist")
                        .appendQueryParameter(Conversation.ConversationType.PRIVATE.getName(), "false") //设置私聊会话是否聚合显示
                        .appendQueryParameter(Conversation.ConversationType.GROUP.getName(), "false")//群组
                        .appendQueryParameter(Conversation.ConversationType.PUBLIC_SERVICE.getName(), "false")//公共服务号
                        .appendQueryParameter(Conversation.ConversationType.APP_PUBLIC_SERVICE.getName(), "false")//订阅号
                        .appendQueryParameter(Conversation.ConversationType.SYSTEM.getName(), "true")//系统
                        .build();
                mConversationsTypes = new Conversation.ConversationType[]{Conversation.ConversationType.PRIVATE,
                        Conversation.ConversationType.GROUP,
                        Conversation.ConversationType.PUBLIC_SERVICE,
                        Conversation.ConversationType.APP_PUBLIC_SERVICE,
                        Conversation.ConversationType.SYSTEM
                };
            }
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }

    protected void initData() {

        final Conversation.ConversationType[] conversationTypes = {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP, Conversation.ConversationType.SYSTEM,
                Conversation.ConversationType.PUBLIC_SERVICE, Conversation.ConversationType.APP_PUBLIC_SERVICE
        };

        RongIM.getInstance().addUnReadMessageCountChangedObserver(this, conversationTypes);
        getConversationPush();// 获取 push 的 id 和 target
        getPushMessage();
    }


    private void getConversationPush() {
        if (getIntent() != null && getIntent().hasExtra("PUSH_CONVERSATIONTYPE") && getIntent().hasExtra("PUSH_TARGETID")) {

            final String conversationType = getIntent().getStringExtra("PUSH_CONVERSATIONTYPE");
            final String targetId = getIntent().getStringExtra("PUSH_TARGETID");


            RongIM.getInstance().getConversation(Conversation.ConversationType.valueOf(conversationType), targetId, new RongIMClient.ResultCallback<Conversation>() {
                @Override
                public void onSuccess(Conversation conversation) {

                    if (conversation != null) {

                        if (conversation.getLatestMessage() instanceof ContactNotificationMessage) { //好友消息的push
                            startActivity(new Intent(MainActivity.this, NewFriendListActivity.class));
                        } else {
                            Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon().appendPath("conversation")
                                    .appendPath(conversationType).appendQueryParameter("targetId", targetId).build();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }
                }

                @Override
                public void onError(RongIMClient.ErrorCode e) {

                }
            });
        }
    }

    /**
     * 得到不落地 push 消息
     */
    private void getPushMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {
            String path = intent.getData().getPath();
            if (path.contains("push_message")) {
                SharedPreferences sharedPreferences = getSharedPreferences("config", MODE_PRIVATE);
                String cacheToken = sharedPreferences.getString("loginToken", "");
                if (TextUtils.isEmpty(cacheToken)) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                } else {
                    if (!RongIM.getInstance().getCurrentConnectionStatus().equals(RongIMClient.ConnectionStatusListener.ConnectionStatus.CONNECTED)) {
                        dialog.show();
                        RongIM.connect(cacheToken, new RongIMClient.ConnectCallback() {
                            @Override
                            public void onTokenIncorrect() {
                                dialog.dismiss();
                            }

                            @Override
                            public void onSuccess(String s) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode e) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        }
    }


    @Override
    public void onCountChanged(int i) {

    }
}
