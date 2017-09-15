package com.hotyi.hotyi.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.GroupInfo;
import com.hotyi.hotyi.other.hotyiClass.GroupMemberInfo;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.hotyi.hotyi.utils.db.GroupMember;
import com.hotyi.hotyi.utils.ui.DemoGridView;
import com.hotyi.hotyi.utils.ui.HeadImageView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.rong.imageloader.core.ImageLoader;
import io.rong.imlib.model.UserInfo;

public class GroupDetailActivity extends BaseUiActivity {

    private TextView name, num;
    private Switch msgSwitch, layoutSwitch;
    private Button button;
    private static final int GROUP_INFO = 130;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private String ryGroupId;
    private GroupInfo groupInfo;
    private GridView memberGridView;
    private List<GroupMemberInfo> groupMemberInfos = new ArrayList<>();
    private boolean isCreated = false;
    private LinearLayout groupMemberLayout;
    private GridAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        name = (TextView) findViewById(R.id.group_detail_name);
        num = (TextView) findViewById(R.id.group_detail_num);
        msgSwitch = (Switch) findViewById(R.id.group_detail_msg_switch);
        layoutSwitch = (Switch) findViewById(R.id.group_detail_layout_switch);
        button = (Button) findViewById(R.id.group_detail_btn);
        groupMemberLayout = (LinearLayout)findViewById(R.id.group_detail_members_click);
        groupMemberLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        memberGridView = (GridView) findViewById(R.id.group_gridview);
        ryGroupId = getIntent().getStringExtra("id");
        asyncTaskManager.request(GROUP_INFO, true, myDataListener);
    }

    OnDataListener myDataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {

            switch (requestCode) {
                case GROUP_INFO:

                    try {
                        String id = MyUserInfo.getInstance().getUserId();
                        String signText = RSAUtil.encryptByPrivateKey("AppRYGroupId=" + ryGroupId + "&" + "UserId=" + id);
                        String login_url = MyAsynctask.HOST + MyAsynctask.GroupInfoByRYGroupId;
                        URL url = new URL(login_url);
                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", id);
                        map.put("AppRYGroupId", ryGroupId);
                        map.put("SignText", signText);
                        if (id == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(GroupDetailActivity.this).post(map, url);
                    } catch (Exception e) {

                    }


                    break;
            }

            return null;
        }

        @Override
        public void onSuccess(int requestCode, Object result) {
            switch (requestCode) {
                case GROUP_INFO:

                    if (result != null) {
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getInt("code") == 1) {
                                JSONObject groupJson = jsonObject.getJSONObject("data");
                                groupInfo = new GroupInfo(groupJson.getString("GroupId"), groupJson.getString("AppRYGroupId")
                                        , groupJson.getString("GroupLogo"), groupJson.getString("GroupName"), groupJson.getString("PeopleNum")
                                        , groupJson.getString("PushMessages"), groupJson.getString("IsManager"));
                                JSONArray membersJsonArray = groupJson.getJSONArray("HeadImgList");
                                int len = membersJsonArray.length();
                                for (int i = 0; i < len; i++) {
                                    JSONObject memberJson = membersJsonArray.getJSONObject(i);
                                    groupMemberInfos.add(new GroupMemberInfo(memberJson.getString("UserId"), memberJson.getString("HeadImage"),
                                            memberJson.getString("UserName")));
                                }
                                name.setText(groupInfo.getGroupName());
                                num.setText("成员(" + len + ")");
                                if (groupInfo.getPushMsg().equals("0"))
                                    msgSwitch.setChecked(true);
                                else
                                    msgSwitch.setChecked(false);
                                if (groupInfo.getIsManager().equals("false")) {
                                    isCreated = false;
                                    button.setText("退出群聊");

                                } else {
                                    isCreated = true;
                                    button.setText("解散群聊");
                                }
                                if (adapter == null) {
                                    adapter = new GridAdapter(GroupDetailActivity.this, groupMemberInfos);
                                    memberGridView.setAdapter(adapter);
                                }

                            }
                        } catch (Exception e) {
                            Log.e("group detail", e.toString());
                        }
                    }

                    break;
            }
        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {

        }
    };


    private class GridAdapter extends BaseAdapter {

        private List<GroupMemberInfo> list;
        Context context;


        public GridAdapter(Context context, List<GroupMemberInfo> list) {
            this.list = list;
            this.context = context;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.group_members_list_item, null);
            }
            HeadImageView iv_avatar = (HeadImageView) convertView.findViewById(R.id.iv_avatar);
            TextView tv_username = (TextView) convertView.findViewById(R.id.tv_username);
            ImageView badge_delete = (ImageView) convertView.findViewById(R.id.badge_delete);

            // 最后一个item，减人按钮
            if (position == getCount() - 1 && isCreated) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.mipmap.member_jianshao_3x);

                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
//                        intent.putExtra("DeleteDiscuId", ryGroupId);
//                        startActivityForResult(intent, 125);
                    }

                });
            } else if ((isCreated && position == getCount() - 2) || (!isCreated && position == getCount() - 1)) {
                tv_username.setText("");
                badge_delete.setVisibility(View.GONE);
                iv_avatar.setImageResource(R.mipmap.member_tianjia_3x);

                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        Intent intent = new Intent(GroupDetailActivity.this, SelectFriendsActivity.class);
//                        intent.putExtra("AddDiscuId", ryGroupId);
//                        startActivityForResult(intent, 126);

                    }
                });
            } else { // 普通成员
                GroupMemberInfo bean = list.get(position);
                Log.e("group detail ", bean.getName());
                tv_username.setText(bean.getName());


                ImageLoader.getInstance().displayImage(bean.getHeadImg(), iv_avatar);
                iv_avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }

                });

            }

            return convertView;
        }

        @Override
        public int getCount() {
            if (isCreated) {
                return list.size() + 2;
            } else {
                return list.size() + 1;
            }
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }
    }
}
