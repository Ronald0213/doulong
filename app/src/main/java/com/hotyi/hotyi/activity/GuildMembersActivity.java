package com.hotyi.hotyi.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.GuildMembersInfo;
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
import java.util.concurrent.BrokenBarrierException;

import io.rong.imageloader.core.ImageLoader;

public class GuildMembersActivity extends BaseUiActivity {
    private ListView leadersListView, membersListView, managerListView;
    private LinearLayout leaderTile, managerTitle, memberTitle;
    private FrameLayout searchLayout;
    private ImageView backImg;
    private EditText searchEdt;
    private static final int GUILD_MEMBERS = 101;//工会成员列表
    private static final int SEARCH_MEMBERS = 102;//搜索成员列表
    private static final int CHANGE_LEADER = 105;//会长转让
    private static final int CANCEL_MANAGER = 106;//免除管理员
    private static final int APPOINT_MANAGER = 107;//任命管理员
    private static final int CANCEL_MEMBER = 108;//移除成员
    private static final int CANCEL_GUILD = 109;//退出公会
    private static final int WATCH_INFO = 200;//查看资料
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private ImageLoader imageLoader = ImageLoader.getInstance();
    private List<GuildMembersInfo> leadersList = new ArrayList<>();
    private List<GuildMembersInfo> membersList = new ArrayList<>();
    private List<GuildMembersInfo> managersList = new ArrayList<>();
    private GuildMemberAdapter leaderAdaper, managerAdapter, membersAdapter;
    private String userId = MyUserInfo.getInstance().getUserId();
    private String guildId;
    private String myIdentity;
    private String searchName;
    private String clickMemeberId;
    private String identity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guild_members);
        guildId = getIntent().getStringExtra("guildId");
        leadersListView = (ListView) findViewById(R.id.guild_members_leaders_list);
        membersListView = (ListView) findViewById(R.id.guild_members_member_list);
        backImg = (ImageView) findViewById(R.id.guild_members_back);
        searchLayout = (FrameLayout) findViewById(R.id.guild_members_search);
        searchEdt = (EditText) findViewById(R.id.guild_members_search_edt);
        managerListView = (ListView) findViewById(R.id.guild_members_manager_list);
        leaderTile = (LinearLayout) findViewById(R.id.guild_members_leader_title);
        managerTitle = (LinearLayout) findViewById(R.id.guild_members_manager_title);
        memberTitle = (LinearLayout) findViewById(R.id.guild_members_member_title);

        leaderAdaper = new GuildMemberAdapter(leadersList);
        managerAdapter = new GuildMemberAdapter(managersList);
        membersAdapter = new GuildMemberAdapter(membersList);
        asyncTaskManager.request(GUILD_MEMBERS, true, myDataListener);
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String str = searchEdt.getText().toString();
                if (str.length() != 0) {
                    Log.e("members ", " done not null");
                    searchName = str;
                    asyncTaskManager.request(SEARCH_MEMBERS, true, myDataListener);
                } else {
                    Log.e("members ", " done null");
                    asyncTaskManager.request(GUILD_MEMBERS, true, myDataListener);
                }


            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        leadersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (myIdentity.equals("1")) {
                    int num = i - leadersListView.getHeaderViewsCount();
                    clickMemeberId = leadersList.get(num).getUserId();
                    identity = leadersList.get(num).getIdentity();
//                Intent intent = new Intent(GuildMembersActivity.this, MyDialogActivity.class);
//                intent.putExtra("myIdentity", myIdentity);
//                intent.putExtra("userId", membersId);
//                intent.putExtra("identity", identity);
//                intent.putExtra("type", "guild");
//                startActivityForResult(intent, 110);
                    setDialog();
                }
            }
        });
        managerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (!myIdentity.equals("1")) {
                    int num = i - managerListView.getHeaderViewsCount();
                    clickMemeberId = managersList.get(num).getUserId();
                    identity = managersList.get(num).getIdentity();
//                    Intent intent = new Intent(GuildMembersActivity.this, MyDialogActivity.class);
//                    intent.putExtra("myIdentity", myIdentity);
//                    intent.putExtra("identity", identity);
//                    intent.putExtra("userId", membersId);
//                    intent.putExtra("type", "guild");
//                    startActivityForResult(intent, 110);
                    setDialog();
                }
            }
        });
        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int num = i - membersListView.getHeaderViewsCount();
                clickMemeberId = membersList.get(num).getUserId();
                identity = membersList.get(num).getIdentity();
//                Intent intent = new Intent(GuildMembersActivity.this, MyDialogActivity.class);
//                intent.putExtra("myIdentity", myIdentity);
//                Log.e("myIdentity",myIdentity);
//                intent.putExtra("membersId", membersId);
//                intent.putExtra("identity",identity);
//                intent.putExtra("type", "guild");
//                startActivityForResult(intent, 110);
                setDialog();
            }
        });
    }

    OnDataListener myDataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode) {
                case CANCEL_GUILD:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.LeaveGuild;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
                case CHANGE_LEADER:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "ToUserId=" + clickMemeberId + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.Owner2Other;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("ToUserId", clickMemeberId);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
                case CANCEL_MANAGER:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "ToUserId=" + clickMemeberId + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.RemoveAdministrator;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("ToUserId", clickMemeberId);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
                case APPOINT_MANAGER:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "ToUserId=" + clickMemeberId + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.AppointedAdministrator;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("ToUserId", clickMemeberId);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
                case CANCEL_MEMBER:
                    try {
                        String memberJsonString = "[{\"UserId/\":\"" + clickMemeberId + "\",\"Identity\":" + identity + "}]";
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "MemberArray=" + memberJsonString + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.RemoveGuildFriend;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("MemberArray", memberJsonString);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
                    } catch (Exception e) {

                    }
                    break;

                case SEARCH_MEMBERS:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "key=" + searchName + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.GuildMemberSearch;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("key", searchName);
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
                case GUILD_MEMBERS:
                    try {
                        String signText = RSAUtil.encryptByPrivateKey("GuildId=" + guildId + "&" + "pageindex=" + "1" + "&" + "pageSize=" + "30" + "&" + "UserId=" + userId);
                        String login_url = MyAsynctask.HOST + MyAsynctask.GuildMemberIndex;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", userId);
                        map.put("GuildId", guildId);
                        map.put("pageindex", "1");
                        map.put("pageSize", "30");
                        map.put("SignText", signText);
                        if (userId == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GuildMembersActivity.this).post(map, url);
//                    return HotyiHttpConnection.getInstance(getApplicationContext()).post();
                    } catch (Exception e) {

                    }
                    break;
            }
            return null;
        }

        @Override
        public void onSuccess(int requestCode, Object result) {
            switch (requestCode) {
                case CHANGE_LEADER:

                    break;
                case CANCEL_MANAGER:

                    break;
                case APPOINT_MANAGER:

                    break;
                case CANCEL_MEMBER:
                    if (result != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                        } catch (Exception e) {

                        }
                    }
                    break;
                case CANCEL_GUILD:
                    if (result != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1){
                                leaderAdaper.notifyDataSetChanged();
                                managerAdapter.notifyDataSetChanged();
                                membersAdapter.notifyDataSetChanged();
                                startActivity(new Intent(GuildMembersActivity.this,MainActivity.class));
                                finish();
                            }

                        } catch (Exception e) {

                        }
                    }
                case SEARCH_MEMBERS:
                    if (result != null) {
                        try {
                            initInfo();
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1) {
                                JSONObject guildMembersJson = jsonObject.getJSONObject("data");
                                myIdentity = guildMembersJson.getString("MyIdentity");
                                Log.e("myIdentity", myIdentity);
                                JSONArray leadersJsonArray = guildMembersJson.getJSONArray("ManagerList");
                                int len = leadersJsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject leaderJson = leadersJsonArray.getJSONObject(i);
                                    leadersList.add(new GuildMembersInfo(leaderJson.getString("NickName"), leaderJson.getString("HeadImage"), leaderJson.getString("UserId"), leaderJson.getString("Identity")));
                                }
                                JSONArray membersJsonArray = guildMembersJson.getJSONArray("MemberList");
                                int length = membersJsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject memberJson = membersJsonArray.getJSONObject(i);
                                    membersList.add(new GuildMembersInfo(memberJson.getString("NickName"), memberJson.getString("HeadImage"), memberJson.getString("UserId"), memberJson.getString("Identity")));
                                }
                                JSONArray managerJsonArray = guildMembersJson.getJSONArray("OtherManagerList");
                                int num = managerJsonArray.length();
                                for (int i = 0; i < num; i++) {
                                    JSONObject memberJson = managerJsonArray.getJSONObject(i);
                                    managersList.add(new GuildMembersInfo(memberJson.getString("NickName"), memberJson.getString("HeadImage"), memberJson.getString("UserId"), memberJson.getString("Identity")));
                                }
                                if (len == 0)
                                    leaderTile.setVisibility(View.GONE);
                                if (length == 0)
                                    memberTitle.setVisibility(View.GONE);
                                if (num == 0)
                                    managerTitle.setVisibility(View.GONE);
                                managerAdapter.notifyDataSetChanged();
                                leaderAdaper.notifyDataSetChanged();
                                membersAdapter.notifyDataSetChanged();
                                ListViewUtil.setListViewHeightBasedOnChildren(leadersListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(membersListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(managerListView);

                            }
                        } catch (Exception e) {
                            Log.e("guild members", " done  " + e.toString());
                        }
                    }
                    break;
                case GUILD_MEMBERS:
                    Log.e("guild members", " done  1");
                    if (result != null) {
                        try {
                            initInfo();
                            Log.e("guild members", " done  2");
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1) {
                                JSONObject guildMembersJson = jsonObject.getJSONObject("data");
                                myIdentity = guildMembersJson.getString("MyIdentity");
                                JSONArray leadersJsonArray = guildMembersJson.getJSONArray("ManagerList");
                                int len = leadersJsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject leaderJson = leadersJsonArray.getJSONObject(i);
                                    leadersList.add(new GuildMembersInfo(leaderJson.getString("NickName"), leaderJson.getString("HeadImage"), leaderJson.getString("UserId"), leaderJson.getString("Identity")));
                                }
                                JSONArray membersJsonArray = guildMembersJson.getJSONArray("MemberList");
                                int length = membersJsonArray.length();
                                for (int i = 0; i < length; i++) {
                                    JSONObject memberJson = membersJsonArray.getJSONObject(i);
                                    membersList.add(new GuildMembersInfo(memberJson.getString("NickName"), memberJson.getString("HeadImage"), memberJson.getString("UserId"), memberJson.getString("Identity")));
                                }
                                JSONArray managerJsonArray = guildMembersJson.getJSONArray("OtherManagerList");
                                int num = managerJsonArray.length();
                                for (int i = 0; i < num; i++) {
                                    JSONObject memberJson = managerJsonArray.getJSONObject(i);
                                    managersList.add(new GuildMembersInfo(memberJson.getString("NickName"), memberJson.getString("HeadImage"), memberJson.getString("UserId"), memberJson.getString("Identity")));
                                }
                                if (len == 0)
                                    leaderTile.setVisibility(View.GONE);
                                if (length == 0)
                                    memberTitle.setVisibility(View.GONE);
                                if (num == 0)
                                    managerTitle.setVisibility(View.GONE);
                                managerListView.setAdapter(managerAdapter);
                                leadersListView.setAdapter(leaderAdaper);
                                membersListView.setAdapter(membersAdapter);
                                ListViewUtil.setListViewHeightBasedOnChildren(leadersListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(membersListView);
                                ListViewUtil.setListViewHeightBasedOnChildren(managerListView);

                            }
                        } catch (Exception e) {
                            Log.e("guild members", " done  " + e.toString());
                        }
                    }
                    break;
            }
        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {

        }
    };

    void initInfo() {
        leadersList.clear();
        managersList.clear();
        membersList.clear();
        leaderTile.setVisibility(View.VISIBLE);
        managerTitle.setVisibility(View.VISIBLE);
        memberTitle.setVisibility(View.VISIBLE);
    }

    class MemberViewHolder {
        HeadImageView headImageView;
        TextView nameText;
    }

    class GuildMemberAdapter extends BaseAdapter {

        List<GuildMembersInfo> infos = new ArrayList<>();

        GuildMemberAdapter(List<GuildMembersInfo> list) {
            infos = list;
        }

        @Override
        public int getCount() {
            return infos.size();
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
            MemberViewHolder viewHolder = null;
            if (view == null) {
                viewHolder = new MemberViewHolder();
                view = LayoutInflater.from(GuildMembersActivity.this).inflate(R.layout.guild_members_list_item, null);
                viewHolder.headImageView = (HeadImageView) view.findViewById(R.id.guild_members_item_head_image);
                viewHolder.nameText = (TextView) view.findViewById(R.id.guild_members_item_name);
                view.setTag(viewHolder);
            } else
                viewHolder = (MemberViewHolder) view.getTag();
            imageLoader.displayImage(infos.get(i).getHeadImg(), viewHolder.headImageView);
            viewHolder.nameText.setText(infos.get(i).getName());
            return view;
        }
    }


    private void setDialog() {
        final Dialog mCameraDialog = new Dialog(this, R.style.guildTeme);
        final LinearLayout root = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.guild_control_layout, null);
        //初始化视图
        final LinearLayout leaderClick = (LinearLayout) root.findViewById(R.id.guild_leader_click);
        final LinearLayout managerCotrol = (LinearLayout) root.findViewById(R.id.guild_leader_manager_click);
        final LinearLayout cancelMember = (LinearLayout) root.findViewById(R.id.guild_members_cancel);
        final LinearLayout userDetaiInfo = (LinearLayout) root.findViewById(R.id.guild_members_info_watch);
        final LinearLayout cancelClick = (LinearLayout) root.findViewById(R.id.guild_control_cancel_action);
        final TextView managerText = (TextView) root.findViewById(R.id.guild_manager_control_text);
        final TextView isMyself = (TextView) root.findViewById(R.id.guild_member_info_ismyself);

        switch (myIdentity) {
            case "1":
                if (identity.equals("2"))
                    managerText.setText("免除管理员");
                break;
            case "2":
                leaderClick.setVisibility(View.GONE);
                managerCotrol.setVisibility(View.GONE);
                break;
            case "3":
                leaderClick.setVisibility(View.GONE);
                managerCotrol.setVisibility(View.GONE);
                cancelMember.setVisibility(View.GONE);
                if (MyUserInfo.getInstance().getUserId().equals(clickMemeberId)) {
                    isMyself.setText("退出公会");
                }
                break;
            default:
                break;
        }
//
        leaderClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myIdentity.equals("1")) {
                    asyncTaskManager.request(CHANGE_LEADER,true,myDataListener);
                } else
                    leaderClick.setVisibility(View.GONE);
            }
        });
        managerCotrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myIdentity.equals("1") && identity.equals("2")) {
//                    Intent resultIntent = new Intent();
//                    intent.putExtra("click", "cancelManager");
//                    intent.putExtra("memberId", membersId);
                    asyncTaskManager.request(CANCEL_MANAGER,true,myDataListener);
//                    setResult(110, resultIntent);
//                    finish();
                } else if (myIdentity.equals("1") && identity.equals("3")) {

                }
            }
        });
        cancelMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!myIdentity.equals("3")) {
//                    Intent resultIntent = new Intent();
//                    intent.putExtra("click", "cancelMember");
//                    intent.putExtra("memberId", membersId);
                    asyncTaskManager.request(CANCEL_MEMBER,true,myDataListener);
                    mCameraDialog.dismiss();
//                    setResult(110, resultIntent);
//                    finish();
                }
            }
        });
        userDetaiInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (MyUserInfo.getInstance().getUserId().equals(clickMemeberId)) {
//                    Intent resultIntent = new Intent();
//                    intent.putExtra("click", "cancelGuild");
//                    intent.putExtra("memberId", membersId);
//                    setResult(110, resultIntent);
                    asyncTaskManager.request(CANCEL_GUILD,true,myDataListener);
                    mCameraDialog.dismiss();
//                    finish();
                } else {
                    Intent resultIntent = new Intent(GuildMembersActivity.this,UserInfoDetailActivity.class);
//                    resultIntent.putExtra("click", "watchInfo");
                    resultIntent.putExtra("UserID", clickMemeberId);
//                    asyncTaskManager.request(WATCH_INFO,true,myDataListener);
//                    setResult(110, resultIntent);
                    startActivity(resultIntent);
                    mCameraDialog.dismiss();
                }
            }
        });
        cancelClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCameraDialog.dismiss();
            }
        });
        mCameraDialog.setContentView(root);
        Window dialogWindow = mCameraDialog.getWindow();
        dialogWindow.setGravity(Gravity.BOTTOM);
//        dialogWindow.setWindowAnimations(R.style.dialogstyle); // 添加动画
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        lp.x = 0; // 新位置X坐标
        lp.y = 0; // 新位置Y坐标
        lp.width = (int) getResources().getDisplayMetrics().widthPixels; // 宽度
        root.measure(0, 0);
        lp.height = root.getMeasuredHeight();
        lp.alpha = 9f; // 透明度
        dialogWindow.setAttributes(lp);
        mCameraDialog.show();
    }


}
