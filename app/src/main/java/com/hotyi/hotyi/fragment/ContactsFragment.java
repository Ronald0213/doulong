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
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
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
import io.rong.imkit.RongContext;
import io.rong.imlib.model.Conversation;

public class ContactsFragment extends Fragment implements OnDataListener{

    private MyUserInfo myUserInfo;
    private AsyncTaskManager asyncTaskManager;
    private TextView guildTextView,groupTextView;
    private List<MyFriendInfo> friendList = new ArrayList<>();
    private List<OfficialInfo> officialList = new ArrayList<>();
    private static final int GET_FRIEND_INFO = 45;
    private ImageLoader imageLoader;
    private ListView friendListView;


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
                    String login_url = MyAsynctask.HOST + MyAsynctask.MyFriendsList;
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
                        if (jsonObject.getString("code").equals("1")) {
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            deFriendJsonInfo(jsonArray);
                        }
                        friendListView.setAdapter(new FriendListAdpter());
                    }
                }catch (Exception e){

                }



                break;
        }
    }

    private void deFriendJsonInfo(JSONArray jsonArray){
        try {
            int len = jsonArray.length();
            if (len == 0){
                return;
            }
            for (int i = 0 ;i < len; i++){
                if (jsonArray.getJSONObject(i).getInt("IsOfficial") == 0) {
                    String userId = jsonArray.getJSONObject(i).getString("UserId");
                    String userName = jsonArray.getJSONObject(i).getString("NickName");
                    String headImange = jsonArray.getJSONObject(i).getString("HeadImage");
                    int sex = jsonArray.getJSONObject(i).getInt("Gender");
                    friendList.add(new MyFriendInfo(userId, userName, headImange,sex));
                }else {
                    String userId = jsonArray.getJSONObject(i).getString("UserId");
                    String userName = jsonArray.getJSONObject(i).getString("NickName");
                    String headImange = jsonArray.getJSONObject(i).getString("HeadImage");
                    int sex = jsonArray.getJSONObject(i).getInt("Gender");
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
            return view;

        }
    }
    class ViewHolder{
       public TextView name;
        public HeadImageView headImageView;
        public ImageView imageView;
    }
}

