package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyGroupInfo;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.ListViewUtil;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Group;

public class MyGuildActivity extends Activity implements OnDataListener{

    private static final int GET_GROUP = 60;
    private ListView groupListView;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(MyGuildActivity.this);
    private MyUserInfo myUserInfo = MyUserInfo.getInstance();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<MyGroupInfo> list = new ArrayList<>();
    private LinearLayout moreGuild;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_guild);
        groupListView = (ListView)findViewById(R.id.guild_list);
        asyncTaskManager.request(GET_GROUP,true,this);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = groupListView.getHeaderViewsCount();
                MyGroupInfo myGroupInfo = list.get(i-num);
//                String id = myGroupInfo.getRongId();
//                RongIM.getInstance().refreshUserInfoCache(new UserInfo(myGroupInfo.getRongId(),myGroupInfo.getName(), Uri.parse(myGroupInfo.getImgUrl())));
                RongIM.getInstance().startGroupChat(MyGuildActivity.this,myGroupInfo.getRongId(),myGroupInfo.getName());
            }
        });

        moreGuild = (LinearLayout)findViewById(R.id.find_more_guild);
        moreGuild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(MyGuildActivity.this,GameListActivity.class));
            }
        });

    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {

        switch (requestCode){
            case GET_GROUP:
                try {
                    String id = myUserInfo.getUserId();
                    Log.e("contacts","              2   " );
                    String signText = RSAUtil.encryptByPrivateKey("pageindex="+"1"+"&"+"pageSize="+"10"+"&"+"UserId="+id);
                    Log.e("contacts","              sign   " +signText);
                    String login_url = MyAsynctask.HOST + MyAsynctask.MyGuildIndex;
                    URL url = new URL(login_url);
                    Log.e("contacts","              3   "+ login_url.toString());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("UserId", id);
                    map.put("pageindex", "1");
                    map.put("pageSize", "10");
                    map.put("SignText",signText);
                    if(id == null){
                        Log.e("RSA",".......");
                    }
                    Log.e("RSA","++++   "+signText);
                    return HotyiHttpConnection.getInstance(MyGuildActivity.this).post(map, url);
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
            case GET_GROUP:
                try {
                    if (result != null){
                        Log.e("group","done  succeed");
                        JSONObject jsonObject =  new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1){
                            JSONArray groupJsonArray = jsonObject.getJSONArray("data");
                            int len  = groupJsonArray.length();
                            for (int i = 0; i < len ; i++) {
                                MyGroupInfo myGroupInfo = new MyGroupInfo();
                                myGroupInfo.setRongId(groupJsonArray.getJSONObject(i).getString("RYGuildId"));
                                myGroupInfo.setName(groupJsonArray.getJSONObject(i).getString("GuildName"));
                                myGroupInfo.setImgUrl(groupJsonArray.getJSONObject(i).getString("Logo"));
                                RongIM.getInstance().refreshGroupInfoCache(new Group(myGroupInfo.getRongId(),myGroupInfo.getName(), Uri.parse(myGroupInfo.getImgUrl())));
                                list.add(myGroupInfo);
                            }
                            groupListView.setAdapter(new GuildListAdpter());
                            ListViewUtil.setListViewHeightBasedOnChildren(groupListView);
                        }

                    }
                }catch (Exception e){

                }

                break;
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    class GuildListAdpter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
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
                view = LayoutInflater.from(MyGuildActivity.this).inflate(R.layout.group_list_item, null);
                viewHolder.name = (TextView) view.findViewById(R.id.group_item_name);
                viewHolder.headImageView = (HeadImageView) view.findViewById(R.id.group_item_head_image);
//                viewHolder.imageView = (ImageView) view.findViewById(R.id.friend_item_sex);
                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();
//            if (list.get(i).getMsex() == 1)
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nan_3x);
//            else
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nv_3x);
            viewHolder.name.setText(list.get(i).getName());
            imageLoader.displayImage(list.get(i).getImgUrl(),viewHolder.headImageView);
            return view;

        }
    }
    class ViewHolder{
        public TextView name;
        public HeadImageView headImageView;
//        public ImageView imageView;
    }
}
