package com.hotyi.hotyi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;

import io.rong.imkit.RongContext;
import io.rong.imlib.model.Conversation;

/**
 * Created by HOTYI on 2017/8/31.
 */

public class UserInfoDetailActivity extends MyBaseActivity implements View.OnClickListener{


    public void startPrivateChat(Context context, String targetUserId, String title) {
        if(context != null && !TextUtils.isEmpty(targetUserId)) {
            if(RongContext.getInstance() == null) {
                throw new ExceptionInInitializerError("RongCloud SDK not init");
            } else {
                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
                context.startActivity(new Intent("android.intent.action.VIEW", uri));
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void onClick(View view) {

    }
}

