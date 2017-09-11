package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.GuildInfo;
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

public class GameDetailInfoActivity extends Activity implements OnDataListener {

    private static final int GET_GAME_DETAIL_INFO = 71;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private ListView guildListView;
    private List<GuildInfo> guildInfos = new ArrayList<>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private String gameId;
    private String userId = MyUserInfo.getInstance().getUserId();
    private ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail_info);
        Intent intent = getIntent();
        gameId = intent.getStringExtra("GameId");
        guildListView = (ListView) findViewById(R.id.game_detail_guild_list);
        back = (ImageView)findViewById(R.id.game_detail_guild_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        asyncTaskManager.request(GET_GAME_DETAIL_INFO, true, this);

    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case GET_GAME_DETAIL_INFO:
                try {
                    if (gameId == null)
                        return null;
                    String sign = RSAUtil.encryptByPrivateKey("GameId="+gameId+"&"+"pageindex="+"1"+"&"+"pageSize="+"10"+
                                                    "&"+"UserId="+userId);
                    String login_url = MyAsynctask.HOST + MyAsynctask.GameInGuildIndex;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("SignText",sign);
                    map.put("UserId",userId);
                    map.put("GameId",gameId);
                    map.put("pageindex", "1");
                    map.put("pageSize", "10");
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                } catch (Exception e) {
                    Log.e("game list", " done " + e.toString());
                }


                break;
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
        switch (requestCode) {
            case GET_GAME_DETAIL_INFO:
                if (result != null ){
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1){
                            JSONArray jsonArray =  jsonObject.getJSONArray("data");
                            int i = 0, len = jsonArray.length();
                            for( ; i < len ; i++){
                                JSONObject guildJson =  jsonArray.getJSONObject(i);
                                guildInfos.add(new GuildInfo(guildJson.getString("GuildId"),guildJson.getString("RYGuildId"),
                                        guildJson.getString("Logo"),guildJson.getString("GuildName"),
                                        guildJson.getString("Introduce"),guildJson.getString("LeaderName"),
                                        guildJson.getString("PeopleNum")));
                            }
                            guildListView.setAdapter(new GuildAdapter());
                            ListViewUtil.setListViewHeightBasedOnChildren(guildListView);
                        }
                    }catch (Exception e){
                        Log.e("guild list",e.toString());
                    }

                }
                break;
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    class GuildAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return guildInfos.size();
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
                view = LayoutInflater.from(GameDetailInfoActivity.this).inflate(R.layout.game_detail_guild_list_item, null);
                viewHolder.guildHead = (HeadImageView) view.findViewById(R.id.guild_detail_item_name);
                viewHolder.num_leader = (TextView) view.findViewById(R.id.game_detail_guild_num_leader);
                viewHolder.name = (TextView) view.findViewById(R.id.game_detail_guild_name);
                viewHolder.introduce = (TextView) view.findViewById(R.id.game_detail_guild_introduce);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }
            imageLoader.displayImage(guildInfos.get(i).getLogo(), viewHolder.guildHead);
            viewHolder.name.setText(guildInfos.get(i).getName());
            Log.e("guild list ","  "+guildInfos.get(i).getMembersNum()+"   "+guildInfos.get(i).getLeaderName());
            viewHolder.num_leader.setText(guildInfos.get(i).getMembersNum() + "/50" + "  会长：" + guildInfos.get(i).getLeaderName());
            viewHolder.introduce.setText(guildInfos.get(i).getIntroduce());
            return view;
        }
    }

    class ViewHolder {

        HeadImageView guildHead;
        TextView name, num_leader, introduce;


    }
}
