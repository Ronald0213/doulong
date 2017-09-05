package com.hotyi.hotyi.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.activity.MyGroupActivity;
import com.hotyi.hotyi.activity.MyGuildActivity;
import com.hotyi.hotyi.activity.UserInfoDetailActivity;
import com.hotyi.hotyi.other.hotyiClass.MyFriendInfo;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.other.hotyiClass.OfficialInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.Inflater;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imageloader.utils.L;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

public class ContactsFragment extends Fragment implements OnDataListener{

    private MyUserInfo myUserInfo;
    private AsyncTaskManager asyncTaskManager;
    private TextView guildTextView,groupTextView;
    private List<MyFriendInfo> friendList = new ArrayList<>();
    private List<OfficialInfo> officialList = new ArrayList<>();
    private static final int GET_FRIEND_INFO = 45;
    private ImageLoader imageLoader;
    private ListView friendListView;
    private ListView officialListView;
    private LinearLayout contactsGroup,contactsGuild;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts,container,false);
        initView(view);
        return view;
    }

    public void initView(View view){
        guildTextView = (TextView)view.findViewById(R.id.contacts_guild_num);
        groupTextView = (TextView)view.findViewById(R.id.contacts_group_numm);
        friendListView = (ListView)view.findViewById(R.id.contacts_list);
        officialListView = (ListView)view.findViewById(R.id.contacts_official_list);
        contactsGroup = (LinearLayout)view.findViewById(R.id.contacts_group);
        contactsGuild = (LinearLayout)view.findViewById(R.id.contacts_guild);
        contactsGuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext().getApplicationContext(), MyGuildActivity.class));

            }
        });

        contactsGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext().getApplicationContext(), MyGroupActivity.class));
            }
        });

        friendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num =  friendListView.getHeaderViewsCount();
                String id = friendList.get(i - num).getUserId();
                Intent intent = new Intent(getContext().getApplicationContext(), UserInfoDetailActivity.class);
                intent.putExtra("UserID",id);
                startActivity(intent);
            }
        });
        officialListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num =  officialListView.getHeaderViewsCount();
                String id = officialList.get(i - num).getUserId();
                Intent intent = new Intent(getContext().getApplicationContext(), UserInfoDetailActivity.class);
                intent.putExtra("UserID",id);
                startActivity(intent);
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        imageLoader = ImageLoader.getInstance();
        myUserInfo = MyUserInfo.getInstance();
        asyncTaskManager = AsyncTaskManager.getInstance(getContext().getApplicationContext());
        Log.e("contacts","              1");
        asyncTaskManager.request(GET_FRIEND_INFO,true,this);
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_FRIEND_INFO:
                try {
                    String id = myUserInfo.getUserId();
                    Log.e("contacts","              2   " );
                    String signText = RSAUtil.encryptByPrivateKey("UserId="+id);
                    Log.e("contacts","              sign   " +signText);
                    String login_url = MyAsynctask.HOST + MyAsynctask.LinkMan;
                    URL url = new URL(login_url);
                    Log.e("contacts","              3   "+ login_url.toString());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("UserId", id);
                    map.put("SignText",signText);
                    if(id == null){
                        Log.e("RSA",".......");
                    }
                    Log.e("RSA","++++   "+signText);
                    return HotyiHttpConnection.getInstance(getContext().getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                } catch (Exception e) {

                }

                break;
        }

        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode){
            case GET_FRIEND_INFO:
                try {
                    if (result != null){
                        String str_result = result.toString();
                        JSONObject jsonObject = new JSONObject(str_result);
                        JSONObject myJsonObject = jsonObject.getJSONObject("data");
                        Log.e("friend","     1   ");
                        if (jsonObject.getString("code").equals("1")) {
                            Log.e("friend","     2   ");
//                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            Log.e("friend","     3   ");
                            JSONArray friendJsonArray = myJsonObject.getJSONArray("FriendListModel");
                            Log.e("friend","     4   ");
                            JSONArray officialJsonArray = myJsonObject.getJSONArray("OfficialUserModel");
                            Log.e("friend","     5   ");
                            deFriendJsonInfo(friendJsonArray);
                            deFriendJsonInfo(officialJsonArray);
                        }
                        friendListView.setAdapter(new FriendListAdpter());
                        Log.e("friend","     6   ");
                        officialListView.setAdapter(new OfficialListAdpter());
                        Log.e("friend","     7   ");
                        groupTextView.setText(myJsonObject.getString("MyGroupCount"));
                        guildTextView.setText(myJsonObject.getString("MyGuildCount"));
                    }
                }catch (Exception e){

                }



                break;
        }
    }

    private void deFriendJsonInfo(JSONArray jsonArray){
        try {
            int len = jsonArray.length();
            Log.e("friend","     1+   ");
            if (len == 0)
                return;
            for (int i = 0 ;i < len; i++){
                if (jsonArray.getJSONObject(i).getInt("IsOfficial") == 0) {
                    Log.e("friend","     2+   ");
                    String userId = jsonArray.getJSONObject(i).getString("UserId");
                    String userName = jsonArray.getJSONObject(i).getString("NickName");
                    String headImange = jsonArray.getJSONObject(i).getString("HeadImage");
                    String rongID = jsonArray.getJSONObject(i).getString("RYAccount");
                    int sex = jsonArray.getJSONObject(i).getInt("Gender");
                    Log.e("friend","     3+   ");
                    //******刷新用户信息缓存
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(rongID,userName,Uri.parse(headImange)));
                    friendList.add(new MyFriendInfo(userId, userName, headImange,sex));
                }else {
                    Log.e("friend","     ++   ");
                    String userId = jsonArray.getJSONObject(i).getString("UserId");
                    String userName = jsonArray.getJSONObject(i).getString("NickName");
                    String headImange = jsonArray.getJSONObject(i).getString("HeadImage");
                    int sex = jsonArray.getJSONObject(i).getInt("Gender");
                    String rongID = jsonArray.getJSONObject(i).getString("RYAccount");
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(rongID,userName,Uri.parse(headImange)));
                    officialList.add(new OfficialInfo(userId, userName, headImange,sex));
                }
            }
        }catch (JSONException e){
            Log.e("contancts",e.toString());
        }

    }


    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
    class FriendListAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return friendList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.friend_list_item, null);
                viewHolder.name = (TextView) view.findViewById(R.id.friend_item_name);
                viewHolder.headImageView = (HeadImageView) view.findViewById(R.id.friend_item_head_image);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.friend_item_sex);
                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();
            if (friendList.get(i).getMsex() == 1)
                viewHolder.imageView.setBackgroundResource(R.mipmap.nan_3x);
            else
                viewHolder.imageView.setBackgroundResource(R.mipmap.nv_3x);
            viewHolder.name.setText(friendList.get(i).getUserName());
            imageLoader.displayImage(friendList.get(i).getHeadImage(),viewHolder.headImageView);
            Log.e("friend pro "," check");
            return view;

        }
    }
    class ViewHolder{
       public TextView name;
        public HeadImageView headImageView;
        public ImageView imageView;
    }

    class OfficialListAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return officialList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new ViewHolder();
                view = LayoutInflater.from(getContext().getApplicationContext()).inflate(R.layout.official_list_item, null);
                viewHolder.name = (TextView) view.findViewById(R.id.official_item_name);
                viewHolder.headImageView = (HeadImageView) view.findViewById(R.id.official_item_head_image);
                viewHolder.imageView = (ImageView) view.findViewById(R.id.official_item_sex);
                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();
            if (officialList.get(i).getMsex() == 1)
                viewHolder.imageView.setBackgroundResource(R.mipmap.nan_3x);
            else
                viewHolder.imageView.setBackgroundResource(R.mipmap.nv_3x);
            viewHolder.name.setText(officialList.get(i).getUserName());
            imageLoader.displayImage(officialList.get(i).getHeadImage(),viewHolder.headImageView);
            return view;
        }
    }
}

