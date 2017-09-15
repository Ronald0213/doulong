package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.GameInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.ListViewUtil;
import com.hotyi.hotyi.utils.MyAsynctask;
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

public class GameListActivity extends BaseUiActivity implements OnDataListener {

    private ListView hotGameListView, otherGameListView;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(GameListActivity.this);
    private static final int GET_GAME_LIST = 70;
    private List<GameInfo> hotGameList = new ArrayList<>();
    private List<GameInfo> otherGameList = new ArrayList<>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView myGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            View decorView = getWindow().getDecorView();
//            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
//            decorView.setSystemUiVisibility(option);
//            getWindow().setStatusBarColor(Color.TRANSPARENT); //也可以设置成灰色透明的，比较符合Material Design的风格
        }
        setContentView(R.layout.activity_game_list);
        hotGameListView = (ListView) findViewById(R.id.hot_game_list);
        myGame = (TextView) findViewById(R.id.game_list_my_game);
        otherGameListView = (ListView) findViewById(R.id.other_game_list);
        myGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GameListActivity.this,MyGameActivity.class));
            }
        });
        hotGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = hotGameList.get(i - hotGameListView.getHeaderViewsCount()).getGameId();
                Intent intent  =  new Intent(GameListActivity.this,GameDetailInfoActivity.class);
                intent.putExtra("GameId",id);
                startActivity(intent);
            }
        });
        otherGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = otherGameList.get(i - otherGameListView.getHeaderViewsCount()).getGameId();
                Intent intent  =  new Intent(GameListActivity.this,GameDetailInfoActivity.class);
                intent.putExtra("GameId",id);
                startActivity(intent);
            }
        });
        asyncTaskManager.request(GET_GAME_LIST, true, this);

    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case GET_GAME_LIST:
                try {
                    String login_url = MyAsynctask.HOST + MyAsynctask.HotGame;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("pageindex", "1");
                    map.put("pageSize", "30");
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
            case GET_GAME_LIST:
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1) {
                            JSONArray hotGameJsonArray = jsonObject.getJSONObject("data").getJSONArray("HotGameList");
                            int i = 0, len = hotGameJsonArray.length();
                            for ( ; i < len ; i++) {
                                JSONObject hotGame = hotGameJsonArray.getJSONObject(i);
                                hotGameList.add(new GameInfo(hotGame.getString("GameId"), hotGame.getString("GameName"), hotGame.getString("Logo")));
                            }
                            JSONArray otherGameJsonArray = jsonObject.getJSONObject("data").getJSONArray("NotHotGameList");
                            int num = 0, length = otherGameJsonArray.length();
                            for (; num < length; num++) {
                                JSONObject otherGame = otherGameJsonArray.getJSONObject(num);
                                otherGameList.add(new GameInfo(otherGame.getString("GameId"), otherGame.getString("GameName"), otherGame.getString("Logo")));
                            }
                            hotGameListView.setAdapter(new HotGameAdapter());
                            otherGameListView.setAdapter(new OtherGameAdapter());
                            ListViewUtil.setListViewHeightBasedOnChildren(hotGameListView);
                            ListViewUtil.setListViewHeightBasedOnChildren(otherGameListView);
                        }
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                }
                break;
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    class GameViewHolder {
        public HeadImageView img;
        public TextView name;
    }

    class HotGameAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return hotGameList.size();
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
            GameViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new GameViewHolder();
                view = LayoutInflater.from(GameListActivity.this).inflate(R.layout.group_list_item, null);
                viewHolder.name = (TextView) view.findViewById(R.id.group_item_name);
                viewHolder.img = (HeadImageView) view.findViewById(R.id.group_item_head_image);
//                viewHolder.imageView = (ImageView) view.findViewById(R.id.friend_item_sex);
                view.setTag(viewHolder);
            } else
                viewHolder = (GameViewHolder) view.getTag();
//            if (list.get(i).getMsex() == 1)
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nan_3x);
//            else
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nv_3x);
            viewHolder.name.setText(hotGameList.get(i).getGameName());
            imageLoader.displayImage(hotGameList.get(i).getGameLogo(), viewHolder.img);
            return view;
        }
    }

    class OtherGameViewHolder {
        public HeadImageView img;
        public TextView name;
    }

    class OtherGameAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return otherGameList.size();
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
            OtherGameViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new OtherGameViewHolder();
                view = LayoutInflater.from(GameListActivity.this).inflate(R.layout.group_list_item, null);
                viewHolder.name = (TextView) view.findViewById(R.id.group_item_name);
                viewHolder.img = (HeadImageView) view.findViewById(R.id.group_item_head_image);
//                viewHolder.imageView = (ImageView) view.findViewById(R.id.friend_item_sex);
                view.setTag(viewHolder);
            } else
                viewHolder = (OtherGameViewHolder) view.getTag();
//            if (list.get(i).getMsex() == 1)
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nan_3x);
//            else
//                viewHolder.imageView.setBackgroundResource(R.mipmap.nv_3x);
            viewHolder.name.setText(otherGameList.get(i).getGameName());
            imageLoader.displayImage(otherGameList.get(i).getGameLogo(), viewHolder.img);
            return view;
        }
    }


}
