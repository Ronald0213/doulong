package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.other.hotyiClass.UserSignRankInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
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

public class SignActivity extends BaseUiActivity implements OnDataListener{

    private TextView daysNum;
    private ListView signRankListView;
    private List<UserSignRankInfo> signRankInfos = new ArrayList<>();
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private static final int SIGN_RANK = 92;
    private String guildId;
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign);
        daysNum = (TextView)findViewById(R.id.sign_days_num);
        signRankListView = (ListView)findViewById(R.id.sign_rank_list);
        guildId = getIntent().getStringExtra("guildId");
        asyncTaskManager.request(SIGN_RANK,true,this);

    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case SIGN_RANK:
                try {
                    String login_url = MyAsynctask.HOST + MyAsynctask.GuildSignedRankingList;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    String sign = RSAUtil.encryptByPrivateKey("GuildId="+guildId+"&"+"pageindex=1"+"&"+"pageSize=30"+"&"
                    +"UserId="+ MyUserInfo.getInstance().getUserId()+"&"+"way=2");
                    map.put("pageindex", "1");
                    map.put("pageSize", "30");
                    map.put("GuildId", guildId);
                    map.put("way", "2");
                    map.put("SignText", sign);
                    map.put("UserId", MyUserInfo.getInstance().getUserId());
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
            switch (requestCode){
                case SIGN_RANK:
                    if (result!=null){
                        try{
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                JSONObject signJson = jsonObject.getJSONObject("data");
                                daysNum.setText(signJson.getString("MyDay"));
                                JSONArray signListJsonArray = signJson.getJSONArray("RankingModelList");
                                int len = signListJsonArray.length();
                                for (int i = 0;i < len; i ++){
                                    JSONObject memberJson = signListJsonArray.getJSONObject(i);
                                    signRankInfos.add(new UserSignRankInfo(memberJson.getString("NickName"),memberJson.getString("HeadImage"),memberJson.getString("Number")));
                                }
                                signRankListView.setAdapter(new SignRankAdapter());
                                Log.e("sign","   "+len);
                            }
                        }catch (Exception e){
                            Log.e("sign","   "+e.toString());
                        }
                    }
                    break;
            }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
    class SignViewHolder{
        HeadImageView headImageView;
        TextView name,days;
    }

    class SignRankAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return signRankInfos.size();
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
            SignViewHolder  viewHolder = null;
            if (view == null){
                viewHolder = new SignViewHolder();
                view = LayoutInflater.from(SignActivity.this).inflate(R.layout.sign_rank_list_item,null);
                viewHolder.headImageView = (HeadImageView)view.findViewById(R.id.sign_list_item_head_image);
                viewHolder.name = (TextView)view.findViewById(R.id.sign_rank_item_name);
                viewHolder.days = (TextView)view.findViewById(R.id.sign_list_item_days);
                view.setTag(viewHolder);
            }else
                viewHolder = (SignViewHolder)view.getTag();
            imageLoader.displayImage(signRankInfos.get(i).getHeadImg(),viewHolder.headImageView);
            viewHolder.name.setText(signRankInfos.get(i).getName());
            viewHolder.days.setText(signRankInfos.get(i).getDays()+"天");
            return view;
        }
    }
}
