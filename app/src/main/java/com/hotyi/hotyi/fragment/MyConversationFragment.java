package com.hotyi.hotyi.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.activity.MyGameActivity;
import com.hotyi.hotyi.utils.adpter.ConversationListAdapterEx;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationListFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imlib.model.Conversation;


public class MyConversationFragment extends Fragment {
    public ConversationListFragment mConversationListFragment = null;
    public Conversation.ConversationType[] mConversationsTypes = null;

    public MyConversationFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_conversation, container, false);
        Fragment fragment = initConversationList();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_conversation, fragment).commit();
        return view;
    }

    private Fragment initConversationList() {
        if (mConversationListFragment == null) {
            ConversationListFragment listFragment = new ConversationListFragment();
            listFragment.setAdapter(new ConversationListAdapterEx(RongContext.getInstance()));
            Uri uri;
            uri = Uri.parse("rong://" + getContext().getApplicationInfo().packageName).buildUpon()
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
            listFragment.setUri(uri);
            mConversationListFragment = listFragment;
            RongIM.setConversationListBehaviorListener(new RongIM.ConversationListBehaviorListener() {
                @Override
                public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
                    return false;
                }

                @Override
                public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
                    return false;
                }

                @Override
                public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
                    Log.e("conversationType", uiConversation.getConversationType().toString());
//                    if (uiConversation.getConversationType().equals(Conversation.ConversationType.SYSTEM))
//                        startActivity(new Intent(context, SubConversationListActivity.class));
                    return false;
                }

                @Override
                public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {

                    return false;
                }
            });
            return listFragment;
        } else {
            return mConversationListFragment;
        }
    }
}
