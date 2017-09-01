package com.hotyi.hotyi.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.other.hotyiClass.UserInfoDetailClass;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.HashMap;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

import static android.view.View.GONE;

/**
 * Created by HOTYI on 2017/8/31.
 */

public class UserInfoDetailActivity extends MyBaseActivity implements View.OnClickListener{

    private ImageView sexImageView,officialImageView;
    private HeadImageView headImageView,gameImageViewOne,gameImageViewTwo,gameImageViewThree,
                            guildImageViewOne,guildImageViewTwo,guildImageViewThree,
                            gameCircleImageViewOne,gameCircleImageViewTwo,gameCircleImageViewThree;
    private TextView userName,userId;
    private Button chatBtn;
    private LinearLayout game,guild,gameCircle;
    private String userIdStr;
    private static final int GET_USER_INFO_DETAIL = 50;
    private MyUserInfo myUserInfo = MyUserInfo.getInstance();
    private UserInfoDetailClass userInfoDetailClass = new UserInfoDetailClass();
    private ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_detail);
        initView();
        Intent intent = getIntent();
        userIdStr = intent.getStringExtra("UserID");
        Log.e("UserInfo",userIdStr);
        mAsyncTaskManager.request(GET_USER_INFO_DETAIL,true,this);
    }


    private void initView(){

        sexImageView = (ImageView) findViewById(R.id.user_info_detail_sex);
        officialImageView = (ImageView) findViewById(R.id.user_info_detail_isofficial);
        headImageView = (HeadImageView)findViewById(R.id.user_info_detail_head_img);
        gameImageViewOne = (HeadImageView)findViewById(R.id.usere_info_detail_game_one);
        gameImageViewTwo = (HeadImageView)findViewById(R.id.usere_info_detail_game_two);
        gameImageViewThree = (HeadImageView)findViewById(R.id.usere_info_detail_game_three);
        guildImageViewOne = (HeadImageView)findViewById(R.id.usere_info_detail_guild_one);
        guildImageViewTwo = (HeadImageView)findViewById(R.id.usere_info_detail_guild_two);
        guildImageViewThree = (HeadImageView)findViewById(R.id.usere_info_detail_guild_three);
        gameCircleImageViewOne = (HeadImageView)findViewById(R.id.usere_info_detail_game_circle_one);
        gameCircleImageViewTwo = (HeadImageView)findViewById(R.id.usere_info_detail_game_circle_two);
        gameCircleImageViewThree = (HeadImageView)findViewById(R.id.usere_info_detail_game_circle_three);
        userName = (TextView)findViewById(R.id.user_info_detail_name);
        userId = (TextView)findViewById(R.id.user_info_detail_hotyi_id);
        chatBtn = (Button)findViewById(R.id.user_info_detail_chat);
        chatBtn.setOnClickListener(this);

    }

//    public void startPrivateChat(Context context, String targetUserId, String title) {
//        if(context != null && !TextUtils.isEmpty(targetUserId)) {
//            if(RongContext.getInstance() == null) {
//                throw new ExceptionInInitializerError("RongCloud SDK not init");
//            } else {
//                Uri uri = Uri.parse("rong://" + context.getApplicationInfo().packageName).buildUpon().appendPath("conversation").appendPath(Conversation.ConversationType.PRIVATE.getName().toLowerCase()).appendQueryParameter("targetId", targetUserId).appendQueryParameter("title", title).build();
//                context.startActivity(new Intent("android.intent.action.VIEW", uri));
//            }
//        } else {
//            throw new IllegalArgumentException();
//        }
//    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {

        switch (requestCode){
            case GET_USER_INFO_DETAIL:

                try {
                    String myUserId = myUserInfo.getUserId();
                    Log.e("contacts","              2   " );
                    String signText = RSAUtil.encryptByPrivateKey("FriendUserId="+userIdStr+"&"+"UserId="+myUserId);
                    Log.e("contacts","              sign   " +signText);
                    String login_url = MyAsynctask.HOST + MyAsynctask.MyFriendInfo;
                    URL url = new URL(login_url);
                    Log.e("contacts","              3   "+ login_url.toString());
                    HashMap<String, String> map = new HashMap<>();
                    map.put("FriendUserId",userIdStr);
                    map.put("UserId", myUserId);
                    map.put("SignText",signText);
                    if(userIdStr == null){
                        Log.e("RSA",".......");
                    }
                    Log.e("RSA","++++   "+signText);
                    return HotyiHttpConnection.getInstance(UserInfoDetailActivity.this).post(map, url);
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
            case GET_USER_INFO_DETAIL:
                try {
                    String result_str = result.toString();
                    JSONObject jsonObject = new JSONObject(result_str);
                    if (jsonObject.getInt("code") == 1){
                        deJsonAndDisPlay(jsonObject);
                        Log.e("userinfo","  done " + userInfoDetailClass.getGameCircleThree());
                    }

                }catch (Exception e){

                }
                break;
        }
    }

    private void deJsonAndDisPlay(JSONObject jsonObject){
        try {
            JSONObject infoJson = jsonObject.getJSONObject("data");
            userInfoDetailClass.setName(infoJson.getString("UserName"));
            userInfoDetailClass.setId(infoJson.getString("UserId"));
            userInfoDetailClass.setRongId(infoJson.getString("RYAccount"));
            Log.e("rongyunID",userInfoDetailClass.getRongId());
            userInfoDetailClass.setHeadImage(infoJson.getString("HeadImage"));
            userInfoDetailClass.setIsMyFrfiend(infoJson.getInt("IsMyFrfiend"));
            userInfoDetailClass.setGender(infoJson.getInt("Gender"));
            userInfoDetailClass.setIsBlack(infoJson.getInt("IsBlack"));
            userInfoDetailClass.setIsOfficial(infoJson.getInt("IsOfficial"));
            JSONArray gameJsonArray = infoJson.getJSONObject("UserGameInfo").getJSONArray("UserGameInfoList");
            int length_game = gameJsonArray.length();
            switch (length_game){
                case 0:
                    gameImageViewOne.setVisibility(GONE);
                    gameImageViewTwo.setVisibility(GONE);
                    gameImageViewThree.setVisibility(GONE);
                    break;
                case 1:
                    userInfoDetailClass.setGameOne(gameJsonArray.getJSONObject(0).getString("GameLogo"));
                    gameImageViewTwo.setVisibility(GONE);
                    gameImageViewThree.setVisibility(GONE);
                    break;
                case 2:
                    userInfoDetailClass.setGameOne(gameJsonArray.getJSONObject(0).getString("GameLogo"));
                    userInfoDetailClass.setGameTwo(gameJsonArray.getJSONObject(1).getString("GameLogo"));
                    gameImageViewThree.setVisibility(GONE);
                    break;
                default:
                    userInfoDetailClass.setGameOne(gameJsonArray.getJSONObject(0).getString("GameLogo"));
                    userInfoDetailClass.setGameTwo(gameJsonArray.getJSONObject(1).getString("GameLogo"));
                    userInfoDetailClass.setGameThree(gameJsonArray.getJSONObject(2).getString("GameLogo"));
                    break;
            }

            JSONArray guildJsonArray = infoJson.getJSONObject("UserGuildInfo").getJSONArray("UserGuildInfoList");
            int length_guild = guildJsonArray.length();
            switch (length_guild){
                case 0:
                    guildImageViewOne.setVisibility(GONE);
                    guildImageViewTwo.setVisibility(GONE);
                    guildImageViewThree.setVisibility(GONE);
                    break;
                case 1:
                    userInfoDetailClass.setGuildOne(guildJsonArray.getJSONObject(0).getString("GuildLogo"));
                    guildImageViewTwo.setVisibility(GONE);
                    guildImageViewThree.setVisibility(GONE);
                    break;
                case 2:
                    userInfoDetailClass.setGuildOne(guildJsonArray.getJSONObject(0).getString("GuildLogo"));
                    userInfoDetailClass.setGuildTwo(guildJsonArray.getJSONObject(1).getString("GuildLogo"));
                    guildImageViewThree.setVisibility(GONE);
                    break;
                default:
                    userInfoDetailClass.setGuildOne(guildJsonArray.getJSONObject(0).getString("GuildLogo"));
                    userInfoDetailClass.setGuildTwo(guildJsonArray.getJSONObject(1).getString("GuildLogo"));
                    userInfoDetailClass.setGuildThree(guildJsonArray.getJSONObject(2).getString("GuildLogo"));
                    break;
            }

            JSONArray gameCircleJsonArray = infoJson.getJSONObject("UserCircleInfo").getJSONArray("CircleImages");
            int length_gameCircle = gameCircleJsonArray.length();
            switch (length_gameCircle){
                case 0:
                    gameCircleImageViewOne.setVisibility(GONE);
                    gameCircleImageViewTwo.setVisibility(GONE);
                    gameCircleImageViewThree.setVisibility(GONE);
                    break;
                case 1:
                    userInfoDetailClass.setGameCircleOne(gameCircleJsonArray.getString(0));
                    gameCircleImageViewTwo.setVisibility(GONE);
                    gameCircleImageViewThree.setVisibility(GONE);
                    break;
                case 2:
                    userInfoDetailClass.setGameCircleOne(gameCircleJsonArray.getString(0));
                    userInfoDetailClass.setGameCircleTwo(gameCircleJsonArray.getString(1));
                    gameCircleImageViewThree.setVisibility(GONE);
                    break;
                default:
                    userInfoDetailClass.setGameCircleOne(gameCircleJsonArray.getString(0));
                    userInfoDetailClass.setGameCircleTwo(gameCircleJsonArray.getString(1));
                    userInfoDetailClass.setGameCircleThree(gameCircleJsonArray.getString(2));
                    break;
            }
            displayImg(length_game,length_guild,length_gameCircle);
        }catch (Exception e){

        }
    }

    public void displayImg(int length_game,int length_guild,int length_gameCircle){
        imageLoader.displayImage(userInfoDetailClass.getHeadImage(),headImageView);
        if (userInfoDetailClass.getGender() == 1){
            sexImageView.setBackgroundResource(R.mipmap.nan_3x);
        }else {
            sexImageView.setBackgroundResource(R.mipmap.nv_3x);
        }
        if (userInfoDetailClass.getIsOfficial() == 0)
            officialImageView.setVisibility(GONE);
        userName.setText(userInfoDetailClass.getName());
        userId.setText("逗龙号："+userInfoDetailClass.getId());
        switch (length_game){
            case 0:
                break;
            case 1:
                imageLoader.displayImage(userInfoDetailClass.getGameOne(),gameImageViewOne);
                break;
            case 2:
                imageLoader.displayImage(userInfoDetailClass.getGameOne(),gameImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGameTwo(),gameImageViewTwo);
                break;
            default:
                imageLoader.displayImage(userInfoDetailClass.getGameOne(),gameImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGameTwo(),gameImageViewTwo);
                imageLoader.displayImage(userInfoDetailClass.getGameThree(),gameImageViewThree);
                break;
        }
        switch (length_guild){
            case 0:
                break;
            case 1:
                imageLoader.displayImage(userInfoDetailClass.getGuildOne(),guildImageViewOne);
                break;
            case 2:
                imageLoader.displayImage(userInfoDetailClass.getGuildOne(),guildImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGuildTwo(),guildImageViewTwo);
                break;
            default:
                imageLoader.displayImage(userInfoDetailClass.getGuildOne(),guildImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGuildTwo(),guildImageViewTwo);
                imageLoader.displayImage(userInfoDetailClass.getGuildThree(),guildImageViewThree);
                break;
        }
        switch (length_gameCircle){
            case 0:
                break;
            case 1:
                imageLoader.displayImage(userInfoDetailClass.getGameCircleOne(),gameCircleImageViewOne);
                break;
            case 2:
                imageLoader.displayImage(userInfoDetailClass.getGameCircleOne(),gameCircleImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGameCircleTwo(),gameCircleImageViewTwo);
                break;
            default:
                imageLoader.displayImage(userInfoDetailClass.getGameCircleOne(),gameCircleImageViewOne);
                imageLoader.displayImage(userInfoDetailClass.getGameCircleTwo(),gameCircleImageViewTwo);
                imageLoader.displayImage(userInfoDetailClass.getGameCircleThree(),gameCircleImageViewThree);
                break;
        }
    }

    @Override
    public void onClick(View view) {
            switch (view.getId()){
                case R.id.user_info_detail_chat:
                    Log.e("rongyunID  11   ",userInfoDetailClass.getRongId());
                    RongIM.getInstance().refreshUserInfoCache(new UserInfo(userInfoDetailClass.getRongId(),userInfoDetailClass.getName(),Uri.parse(userInfoDetailClass.getHeadImage())));
                    RongIM.getInstance().startPrivateChat(UserInfoDetailActivity.this,userInfoDetailClass.getRongId(),userInfoDetailClass.getName());
                    break;
            }
    }
}

