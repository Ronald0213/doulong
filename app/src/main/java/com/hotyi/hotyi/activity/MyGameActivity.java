package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.GameInfo;
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

public class MyGameActivity extends Activity implements OnDataListener {

    private static final int GET_MY_GAME = 77;
    private static final int FOCUS_GAME = 78;
    private static final int CANCEL_FOCUS_GAME = 79;
    private ListView myGameListView, notMyGameListView;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private MyUserInfo myUserInfo = MyUserInfo.getInstance();
    private ImageView back;
    private List<GameInfo> myGameList = new ArrayList<>();
    private List<GameInfo> notMyGameList = new ArrayList<>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private String gameId;
    private GameInfo clickGameInfo ;
    private MyGameAdapter myGameAdapter,notMyGameAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_game);
        myGameListView = (ListView) findViewById(R.id.my_game_list);
        notMyGameListView = (ListView) findViewById(R.id.my_game_other_list);
        back = (ImageView) findViewById(R.id.my_game_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        asyncTaskManager.request(GET_MY_GAME, true, this);
        myGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = i - myGameListView.getHeaderViewsCount();
                clickGameInfo = myGameList.get(num);
                gameId = clickGameInfo.getGameId();
                Log.e("game click", " done 1");
                asyncTaskManager.request(CANCEL_FOCUS_GAME, true, myGameListener);
            }
        });
        notMyGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = i -notMyGameListView.getHeaderViewsCount();
                clickGameInfo = notMyGameList.get(num);
                gameId = clickGameInfo.getGameId();
                asyncTaskManager.request(FOCUS_GAME,true,myGameListener);
            }
        });

    }

    OnDataListener myGameListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode) {
                case FOCUS_GAME:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.AddFocusGameRecord;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String userId = myUserInfo.getUserId();
                        String sign = RSAUtil.encryptByPrivateKey("GameId="+gameId+"&"+"UserId=" + userId);
                        map.put("UserId", userId);
                        map.put("GameId", gameId);
                        map.put("SignText", sign);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                    break;
                case CANCEL_FOCUS_GAME:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.DeleteFocusGameRecord;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String userId = myUserInfo.getUserId();
                        String sign = RSAUtil.encryptByPrivateKey("GameId="+gameId+"&"+"UserId=" + userId);
                        map.put("UserId", userId);
                        map.put("GameId", gameId);
                        map.put("SignText", sign);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
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
                case FOCUS_GAME:
                    if (result != null){
                        try {
                            JSONObject gameJson = new JSONObject(result.toString());
                            if (gameJson.getInt("code") == 1){
                                myGameList.add(clickGameInfo);
                                notMyGameList.remove(clickGameInfo);
                                myGameAdapter.notifyDataSetChanged();
                                notMyGameAdapter.notifyDataSetChanged();
                                ListViewUtil.setListViewHeightBasedOnChildren(myGameListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(notMyGameListView);
                            }
                        }catch (Exception e){
                            Log.e("game list", " done " + e.toString());
                        }

                    }

                    break;
                case CANCEL_FOCUS_GAME:
                    if (result != null){
                        try {
                            JSONObject gameJson = new JSONObject(result.toString());
                            if (gameJson.getInt("code") == 1){
                                myGameList.remove(clickGameInfo);
                                notMyGameList.add(clickGameInfo);
                                myGameAdapter.notifyDataSetChanged();
                                notMyGameAdapter.notifyDataSetChanged();
                                ListViewUtil.setListViewHeightBasedOnChildren(myGameListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(notMyGameListView);
                            }
                        }catch (Exception e){
                            Log.e("game list", " done " + e.toString());
                        }

                    }
                    break;
            }
        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {


        }
    };

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode) {
            case GET_MY_GAME:
                try {
                    String login_url = MyAsynctask.HOST + MyAsynctask.MyFocusGameRecordList;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    String userId = myUserInfo.getUserId();
                    String sign = RSAUtil.encryptByPrivateKey("UserId=" + userId);
                    map.put("UserId", userId);
                    map.put("SignText", sign);
                    return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
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
            case GET_MY_GAME:
                if (result != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1) {
                            JSONArray myGameJsonArray = jsonObject.getJSONObject("data").getJSONArray("FocusGameList");
                            int i = 0, len = myGameJsonArray.length();
                            for (; i < len; i++) {
                                JSONObject myGameJson = myGameJsonArray.getJSONObject(i);
                                if (myGameJson.getString("GameId").equals("28"))
                                    continue;
                                myGameList.add(new GameInfo(myGameJson.getString("GameId"), myGameJson.getString("GameName"), myGameJson.getString("GameLogo")));
                            }
                            JSONArray notMyGameJsonArray = jsonObject.getJSONObject("data").getJSONArray("UnFocusGameList");
                            int num = 0, length = notMyGameJsonArray.length();
                            for (; num < length; num++) {
                                JSONObject notMyGameJson = notMyGameJsonArray.getJSONObject(num);
                                if (notMyGameJson.getString("GameId").equals("28"))
                                    continue;
                                notMyGameList.add(new GameInfo(notMyGameJson.getString("GameId"), notMyGameJson.getString("GameName"), notMyGameJson.getString("GameLogo")));
                            }
                            myGameAdapter =  new MyGameAdapter(myGameList);
                            notMyGameAdapter = new MyGameAdapter(notMyGameList);
                            myGameListView.setAdapter(myGameAdapter);
                            notMyGameListView.setAdapter(notMyGameAdapter);
                            ListViewUtil.setListViewHeightBasedOnChildren(myGameListView);
                            ListViewUtil.setListViewHeightBasedOnChildren(notMyGameListView);
                        }
                    } catch (Exception e) {
                        Log.e("game list", " done  success" + e.toString());
                    }
                }
                break;
        }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    class MyGameAdapter extends BaseAdapter {
        List<GameInfo> myList = new ArrayList<>();

        MyGameAdapter(List<GameInfo> list) {
            myList = list;
        }

        @Override
        public int getCount() {
            return myList.size();
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
                view = LayoutInflater.from(MyGameActivity.this).inflate(R.layout.my_game_list_item, null);
                viewHolder.headImg = (HeadImageView) view.findViewById(R.id.my_game_item_head_image);
                viewHolder.name = (TextView) view.findViewById(R.id.my_game_item_name);
                viewHolder.choose = (ImageView) view.findViewById(R.id.my_game_is_choose);
                view.setTag(viewHolder);
            } else
                viewHolder = (ViewHolder) view.getTag();
            imageLoader.displayImage(myList.get(i).getGameLogo(), viewHolder.headImg);
            viewHolder.name.setText(myList.get(i).getGameName());
            if (myList == myGameList)
                viewHolder.choose.setBackgroundResource(R.mipmap.home_choosefriends_yes_3x);
            else if (notMyGameList == myList)
                viewHolder.choose.setBackgroundResource(R.mipmap.home_choosefriends_no3x);
            return view;
        }
    }

    class ViewHolder {
        HeadImageView headImg;
        TextView name;
        ImageView choose;
    }
}
