package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.other.hotyiClass.MyUserInfo;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.RSAUtil;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class SearchAcriviry extends BaseUiActivity {

    private ImageView back,search;
    private EditText edt;
    private String searchStr;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(this);
    private static final int SEARCH = 131;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_acriviry);
        search = (ImageView)findViewById(R.id.search_click);
        back = (ImageView)findViewById(R.id.search_back);
        edt = (EditText)findViewById(R.id.search_edt);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                asyncTaskManager.request(SEARCH,true,onDataListener);
            }
        });
    }

    OnDataListener onDataListener = new OnDataListener() {
        @Override
        public Object doInBackground(int requestCode, String parameter) throws HttpException {
            switch (requestCode){
                case SEARCH:
                    try {
                        String id = MyUserInfo.getInstance().getUserId();
                        Log.e("contacts", "              2   ");
                        searchStr = edt.getText().toString();
                        if (searchStr.length() == 0 ||searchStr == null){
                            return null;
                        }
                        String str = new String(searchStr.getBytes(),"UTF-8");
                        String key = URLEncoder.encode(str,"UTF-8");
                        String signText = RSAUtil.encryptByPrivateKey("key="+searchStr+"&"+"pageindex=1"+"&"
                                +"pageSize=30"+"&"+"UserId=" + id);
                        String login_url = MyAsynctask.HOST + MyAsynctask.SearchUser;
                        URL url = new URL(login_url);

                        HashMap<String, String> map = new HashMap<>();
                        map.put("UserId", id);
                        map.put("key", searchStr);
//                        map.put("key", key);
                        map.put("pageindex", "1");
                        map.put("pageSize", "30");
                        map.put("SignText", signText);
                        if (id == null) {
                            Log.e("RSA", ".......");
                        }
                        return HotyiHttpConnection.getInstance(SearchAcriviry.this).post(map, url);
                    } catch (Exception e) {

                    }
                    break;
            }
            return null;
        }

        @Override
        public void onSuccess(int requestCode, Object result) {
            switch (requestCode){
                case SEARCH:
                    break;
            }

        }

        @Override
        public void onFailure(int requestCode, int state, Object result) {

        }
    };

}
