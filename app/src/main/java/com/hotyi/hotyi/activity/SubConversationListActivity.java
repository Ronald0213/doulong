package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.other.hotyiClass.SystemMsgInfo;
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
import io.rong.imageloader.utils.L;

public class SubConversationListActivity extends Activity {
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private static final int SYSTEM_MSG = 120;
    private ListView systemListView;
    private List<SystemMsgInfo> systemMsgInfos = new ArrayList<>();
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private static final int JOIN_GUILD = 121;
    private static final int INTO_GUILD = 122;
    private static final int ACCESS_FRIEND = 123;
    private static final int DENY_FRIEND = 124;
    private int joinGuildFlag = 0;
    private int intoGuildFlag = 0;
    private SystemMsgInfo clickSystemMsg;
    private View clickView;
    private SystemAdapter adapter = new SystemAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_conversation_list);
        systemListView = (ListView) findViewById(R.id.system_msg_list);
        asyncTaskManager.request(SYSTEM_MSG, true, myDataListener);
    }

    OnDataListener myDataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode) {
                case SYSTEM_MSG:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.ApplyMsgIndex;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String sign = RSAUtil.encryptByPrivateKey("pageIndex=1" + "&" + "pageSize=10" + "&"
                                + "UserId=" + MyUserInfo.getInstance().getUserId());
                        map.put("pageIndex", "1");
                        map.put("pageSize", "10");
                        map.put("SignText", sign);
                        map.put("UserId", MyUserInfo.getInstance().getUserId());
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                    break;
                case ACCESS_FRIEND:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.AccessFriend;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String sign = RSAUtil.encryptByPrivateKey("FriendUserId=" +clickSystemMsg.getAccount()+ "&"
                                + "UserId=" + MyUserInfo.getInstance().getUserId());
                        map.put("FriendUserId", clickSystemMsg.getAccount());
                        map.put("UserId", MyUserInfo.getInstance().getUserId());
                        map.put("SignText", sign);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                    break;
                case DENY_FRIEND:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.DenyFriend;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String sign = RSAUtil.encryptByPrivateKey("FriendUserId=" +clickSystemMsg.getAccount()+ "&"
                                + "UserId=" + MyUserInfo.getInstance().getUserId());
                        map.put("FriendUserId", clickSystemMsg.getAccount());
                        map.put("UserId", MyUserInfo.getInstance().getUserId());
                        map.put("SignText", sign);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                    break;
                case JOIN_GUILD:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.OptionApplyIntoGuild;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String sign = RSAUtil.encryptByPrivateKey("GuildId=" +clickSystemMsg.getGuildId()+ "&"
                                +"UserId="+clickSystemMsg.getAccount()
                        +"&"+"way="+intoGuildFlag);
                        map.put("GuildId", clickSystemMsg.getGuildId());
                        map.put("UserId", clickSystemMsg.getAccount());
                        map.put("way", String.valueOf(joinGuildFlag));
                        map.put("SignText", sign);
                        return HotyiHttpConnection.getInstance(getApplicationContext()).post(map, url);
                    } catch (Exception e) {
                        Log.e("game list", " done " + e.toString());
                    }
                    break;
                case INTO_GUILD:
                    try {
                        String login_url = MyAsynctask.HOST + MyAsynctask.OptionIntoGuild;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        String sign = RSAUtil.encryptByPrivateKey("GuildId=" +clickSystemMsg.getGuildId()+ "&"
                                + "ManagerId=" + MyUserInfo.getInstance().getUserId()+"&"+"UserId="+clickSystemMsg.getAccount()
                                +"&"+"way="+joinGuildFlag);
                        map.put("GuildId", clickSystemMsg.getGuildId());
                        map.put("ManagerId", MyUserInfo.getInstance().getUserId());
                        map.put("UserId", clickSystemMsg.getAccount());
                        map.put("way", String.valueOf(joinGuildFlag));
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
                case SYSTEM_MSG:
                    if (result != null) {
                        try {
                            systemMsgInfos.clear();
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1) {
                                JSONArray msgJsonArray = jsonObject.getJSONArray("data");
                                int len = msgJsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject msgJson = msgJsonArray.getJSONObject(i);
                                    systemMsgInfos.add(new SystemMsgInfo(msgJson.getString("AMId"), msgJson.getString("MsgType"),
                                            msgJson.getString("MsgStauts"), msgJson.getString("Logo"),
                                            msgJson.getString("Content"), msgJson.getString("Name"),
                                            msgJson.getString("ApplyTime"), msgJson.getString("Account"), msgJson.getString("GuildId")));
                                }
                                systemListView.setAdapter(adapter);
                            }
                        } catch (Exception e) {

                        }
                    }
                    break;
                case DENY_FRIEND:
                    if (result!= null){
                        try{
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                asyncTaskManager.request(SYSTEM_MSG,true,this);
                            }
                        }catch (Exception e){

                        }

                    }
                    break;
                case ACCESS_FRIEND:
                    if (result!= null){
                        try{
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                asyncTaskManager.request(SYSTEM_MSG,true,this);
                            }
                        }catch (Exception e){

                        }

                    }
                    break;
                case JOIN_GUILD:
                    if (result!= null){
                        try{
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                asyncTaskManager.request(SYSTEM_MSG,true,this);
                            }
                        }catch (Exception e){

                        }
                    }
                    break;
                case INTO_GUILD:
                    if (result!= null){
                        try{
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                asyncTaskManager.request(SYSTEM_MSG,true,this);
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

    class SystemViewHolder {
        TextView msgType, name, time, content, done, cancel, sure;
        HeadImageView head;
        LinearLayout click;
    }

    class SystemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return systemMsgInfos.size();
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
        public View getView(int i, View root, ViewGroup viewGroup) {
            SystemViewHolder viewHolder = null;
            if (root == null) {
                viewHolder = new SystemViewHolder();
                root = LayoutInflater.from(SubConversationListActivity.this).inflate(R.layout.system_msg_list_item, null);
                viewHolder.msgType = (TextView) root.findViewById(R.id.system_msg_list_item_msg_type);
                viewHolder.name = (TextView) root.findViewById(R.id.system_msg_list_item_name);
                viewHolder.time = (TextView) root.findViewById(R.id.system_msg_list_item_time);
                viewHolder.content = (TextView) root.findViewById(R.id.system_msg_list_item_content);
                viewHolder.done = (TextView) root.findViewById(R.id.system_msg_list_item_done);
                viewHolder.cancel = (TextView) root.findViewById(R.id.system_msg_list_item_cancel);
                viewHolder.sure = (TextView) root.findViewById(R.id.system_msg_list_item_sure);
                viewHolder.head = (HeadImageView) root.findViewById(R.id.system_msg_list_item_head);
                viewHolder.click = (LinearLayout) root.findViewById(R.id.system_msg_list_item_click);
                root.setTag(viewHolder);
            } else
                viewHolder = (SystemViewHolder) root.getTag();
            final SystemMsgInfo systemMsgInfo = systemMsgInfos.get(i);
            final String msgType = systemMsgInfo.getMsgType();
            final String msgStatus = systemMsgInfo.getMsgStatus();
            switch (msgType) {
                case "1":
                    viewHolder.msgType.setText("好友请求");
                    switch (msgStatus) {
                        case "0":
                            viewHolder.click.setVisibility(View.VISIBLE);
                            break;
                        case "1":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("已添加好友");
                            break;
                        case "2":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("已拒绝添加好友");
                            break;
                    }
                    break;
                case "2":
                    viewHolder.msgType.setText("公会请求");
                    switch (systemMsgInfo.getMsgStatus()) {
                        case "0":
                            viewHolder.click.setVisibility(View.VISIBLE);
                            break;
                        case "1":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("你已通过他的公会请求");
                            break;
                        case "2":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("你已拒绝他的公会清求");
                            break;
                    }
                    break;
                case "3":
                    viewHolder.msgType.setText("公会邀请");
                    switch (systemMsgInfo.getMsgStatus()) {
                        case "0":
                            viewHolder.click.setVisibility(View.VISIBLE);
                            break;
                        case "1":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("你已同意加入公会");
                            break;
                        case "2":
                            viewHolder.done.setVisibility(View.VISIBLE);
                            viewHolder.done.setText("你已拒绝加入公会");
                            break;
                    }
                    break;
            }
            imageLoader.displayImage(systemMsgInfo.getLogo(), viewHolder.head);
            viewHolder.name.setText(systemMsgInfo.getName());
            viewHolder.time.setText(systemMsgInfo.getApplyTime());
            viewHolder.content.setText(systemMsgInfo.getContent());
            viewHolder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSystemMsg = systemMsgInfo;
                    switch (msgType) {
                        case "1":
                            asyncTaskManager.request(DENY_FRIEND, true, myDataListener);
                            break;
                        case "2":
                            joinGuildFlag = 2;
                            asyncTaskManager.request(JOIN_GUILD, true, myDataListener);
                            break;
                        case "3":
                            intoGuildFlag = 2;
                            asyncTaskManager.request(INTO_GUILD, true, myDataListener);
                            break;
                    }
                    Log.e("system msg", " cancel");
                }
            });
            viewHolder.sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickSystemMsg = systemMsgInfo;
                    switch (msgType) {
                        case "1":
                            asyncTaskManager.request(ACCESS_FRIEND, true, myDataListener);
                            break;
                        case "2":
                            joinGuildFlag = 1;
                            asyncTaskManager.request(JOIN_GUILD, true, myDataListener);
                            break;
                        case "3":
                            intoGuildFlag = 1;
                            asyncTaskManager.request(INTO_GUILD, true, myDataListener);
                            break;
                    }
                    Log.e("system msg", " sure" + systemMsgInfo.getName());
                }
            });

            return root;
        }
    }
}
