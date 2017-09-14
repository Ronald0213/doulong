package com.hotyi.hotyi.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.InfoUtils;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import io.rong.imageloader.core.ImageLoader;

public class GuildDetailActivity extends BaseUiActivity implements OnDataListener{

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ImageView background,signImg;
    private TextView activityNum,gameName,guildId,guildLevel,membersNum,guildName,guildIntroduce;
    private LinearLayout guildMembers;
    private ImageView headImageView ;
    private Button chatBtn,cancelBtn;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private LinearLayout signLayout;
    private HeadImageView gameHead;
    private String id,guildIdAStr;
    private Bitmap sign,noSign;
    private static final int GET_GUILD_DETAIL_INFO = 90;
    private static final int SIGN = 91;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("guild detail", " done");
        setContentView(R.layout.activity_guild_detail);
        id = getIntent().getStringExtra("id");
        if (id != null){
            asyncTaskManager.request(GET_GUILD_DETAIL_INFO,true,this);
        }
        initView();
        sign =  BitmapFactory.decodeResource(getResources(),R.mipmap.guild_qiandao_yes);
        noSign =  BitmapFactory.decodeResource(getResources(),R.mipmap.guild_qiandao_no);

    }

    void initView(){
        background = (ImageView)findViewById(R.id.guild_detail_game_background);
        signImg = (ImageView)findViewById(R.id.guild_detail_sign);
        headImageView = (ImageView)findViewById(R.id.guild_detail_guild_head_img);
        gameName = (TextView)findViewById(R.id.guild_detail_game_name);
        guildName = (TextView)findViewById(R.id.guild_detail_guildname);
        guildIntroduce = (TextView)findViewById(R.id.guild_detail_guild_introduce);
        activityNum = (TextView)findViewById(R.id.guild_detail_activity_num);
        guildId = (TextView)findViewById(R.id.guild_detail_guild_id);
        guildLevel = (TextView)findViewById(R.id.guild_detail_guild_level);
        membersNum = (TextView)findViewById(R.id.guild_detail_members_num);
        chatBtn = (Button)findViewById(R.id.guild_detail_chat_btn);
        cancelBtn = (Button)findViewById(R.id.guild_detail_cancel_btn);
        gameHead = (HeadImageView)findViewById(R.id.guild_detail_game_head);
        guildMembers = (LinearLayout)findViewById(R.id.guild_detail_guild_members);
        signLayout = (LinearLayout)findViewById(R.id.guild_detail_sign_click);

        guildMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuildDetailActivity.this,GuildMembersActivity.class);
                intent.putExtra("guildId",guildIdAStr);
                startActivity(intent);
            }
        });

        signLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncTaskManager.request(SIGN,true,dataListener);
            }
        });

    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_GUILD_DETAIL_INFO:
                try {
                    String myUserId = MyUserInfo.getInstance().getUserId();
                    Log.e("contacts", "              2   ");
                    String signText = RSAUtil.encryptByPrivateKey("GuildId=" + id + "&" + "UserId=" + myUserId);
                    Log.e("user", "   done ");
                    String login_url = MyAsynctask.HOST + MyAsynctask.GuildInfo;
                    URL url = new URL(login_url);
                    Log.e("contacts", "              3   " + login_url.toString());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("GuildId", id);
                    map.put("UserId", myUserId);
                    map.put("SignText", signText);

                    Log.e("RSA", "++++   " + signText);
                    return HotyiHttpConnection.getInstance(GuildDetailActivity.this).post(map, url);


//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                } catch (Exception e) {

                }



                break;
        }

        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

        switch (requestCode)
        {
            case GET_GUILD_DETAIL_INFO:

                if (result != null){
                    try {
                        JSONObject jsonObject =  new JSONObject(result.toString());
                        if (jsonObject.getInt("code") == 1){
                            JSONObject guildInfo = jsonObject.getJSONObject("data");
                            imageLoader.displayImage(guildInfo.getString("Logo"),headImageView);
                            imageLoader.displayImage(guildInfo.getString("Cover"),background);
                            imageLoader.displayImage(guildInfo.getString("GameLogo"),gameHead);
                            guildName.setText(guildInfo.getString("GuildName"));
                            guildIntroduce.setText(guildInfo.getString("Introduce"));
                            guildId.setText(guildInfo.getString("GuildId"));
                            guildIdAStr = guildInfo.getString("GuildId");
                            guildLevel.setText(guildInfo.getString("Level"));
                            if (guildInfo.getString("IsSign").equals("0"))
                                signImg.setImageBitmap(noSign);
                            else
                                signImg.setImageBitmap(sign);
                            gameName.setText(guildInfo.getString("GameName"));
                            activityNum.setText("活动（共"+guildInfo.getString("ActiveNum")+"项活动）");
                            membersNum.setText(guildInfo.getString("PeopleNum"));
                        }
                    }catch (Exception e){

                    }
                }

                break;
        }


    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }

    OnDataListener dataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode){
                case SIGN:
                    try {
                        String myUserId = MyUserInfo.getInstance().getUserId();
                        Log.e("contacts", "              2   ");
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildIdAStr + "&" + "UserId=" + myUserId);
                        Log.e("user", "   done ");
                        String login_url = MyAsynctask.HOST + MyAsynctask.GuildSigned;
                        URL url = new URL(login_url);
                        Log.e("contacts", "              3   " + login_url.toString());
                        HashMap<String, String> map = new HashMap<>();
                        map.put("GuildId", guildIdAStr);
                        map.put("UserId", myUserId);
                        map.put("SignText", signText);

                        Log.e("RSA", "++++   " + signText);
                        return HotyiHttpConnection.getInstance(GuildDetailActivity.this).post(map, url);


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
                case SIGN:

                    if (result != null){
                        try{
                            JSONObject jsonObject =  new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                Intent intent = new Intent( GuildDetailActivity.this,SignActivity.class);
                                intent.putExtra("guildId",guildIdAStr);
                                startActivity(intent);
                            }else{
                                Toast.makeText(GuildDetailActivity.this,jsonObject.getString("result_msg"),Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){

                        }
                    }

                    break;
            }

        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {

        }
    };


}
