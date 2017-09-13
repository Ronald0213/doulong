package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.utils.ui.GuildSimpleImageView;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import io.rong.imageloader.core.ImageLoader;

public class GuildSimpleInfoActivity extends BaseUiActivity implements  View.OnClickListener,OnDataListener {
    private static final int JOIN_GUILD = 80;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private String background, head, name, introduce, state, guildId,ryGuildId;
    private GuildSimpleImageView backgroundImg;
    private HeadImageView headImageView;
    private TextView nameText, introduceText;
    private Button sure, cancel;
    private String userId = MyUserInfo.getInstance().getUserId();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild_simple_info);
        Intent intent = getIntent();
        guildId = intent.getStringExtra("guildId");
        background = intent.getStringExtra("background");
        head = intent.getStringExtra("head");
        name  = intent.getStringExtra("name");
        state = intent.getStringExtra("status");
        introduce = intent.getStringExtra("introduce");
        ryGuildId = intent.getStringExtra("ryGuildId");

        Log.e("game simple info" , guildId);
//        intent.putExtra("guildId",guildInfo.getRyGuildId());
//        intent.putExtra("head",guildInfo.getLogo());
//        intent.putExtra("background",guildInfo.getBackground());
//        intent.putExtra("introduce",guildInfo.getIntroduce());
//        intent.putExtra("name",guildInfo.getName());
//        intent.putExtra("status",guildInfo.getStatus());
        initView();
//        if (guildId != null)
//            asyncTaskManager.request(GET_GUILD_SIMPLE_INFO, true, this);
        imageLoader.displayImage(background, backgroundImg);
        imageLoader.displayImage(head, headImageView);
        nameText.setText(name);
        introduceText.setText(introduce);
        if (state.equals("1"))
            sure.setText("查看公会");
        else
            sure.setText("加入公会");
        cancel.setOnClickListener(this);

    }

    public void initView() {
        backgroundImg = (GuildSimpleImageView) findViewById(R.id.guild_simple_info_background);
        headImageView = (HeadImageView) findViewById(R.id.guild_simple_info_head);
        nameText = (TextView) findViewById(R.id.guild_simple_info_name);
        introduceText = (TextView) findViewById(R.id.guild_simple_info_introduce);
        sure = (Button) findViewById(R.id.guild_simple_info_sure);
        cancel = (Button) findViewById(R.id.guild_simple_info_cancel);
        sure.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.guild_simple_info_sure:
                if (state.equals("1")) {
                    Intent intent = new Intent(GuildSimpleInfoActivity.this,GameDetailInfoActivity.class);
                    intent.putExtra("guildId",guildId);
                    finish();
                }
                else
                    asyncTaskManager.request(JOIN_GUILD,true,this);
                break;
            case R.id.guild_simple_info_cancel:
                finish();
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case JOIN_GUILD:
                try {
                    String sign = RSAUtil.encryptByPrivateKey("GuildId="+guildId+"&"+"UserId="+userId);
                    String login_url = MyAsynctask.HOST + MyAsynctask.ApplyIntoGuild;
                    URL url = new URL(login_url);
                    HashMap<String, String> map = new HashMap<>();
                    map.put("UserId", userId);
                    map.put("GuildId", guildId);
                    map.put("SignText",sign);
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
            case JOIN_GUILD:

                if (result != null){
                    try{
                        JSONObject jsonObject = new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1)
                        Log.e("guild simple join "," done ");
                    }catch (Exception e){
                        Log.e("guild simple join "," done "+e.toString());
                    }

                }

                break;
        }

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
}
