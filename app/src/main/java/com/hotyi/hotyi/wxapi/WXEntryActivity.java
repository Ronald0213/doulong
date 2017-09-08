package com.hotyi.hotyi.wxapi;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.activity.LoginActivity;
import com.hotyi.hotyi.utils.HotyiHttpConnection;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.MyAsynctask;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler,OnDataListener {

    private final String APP_ID = "wx4ae76f1d5b08feb3";
    private final String APP_SECRET = "48d6553199bc2a4f8bdf4e118ea705f7";
    private static final int GET_TOKEN = 18;
    private static final int GET_INFO = 19;
    private AsyncTaskManager asyncTaskManager = AsyncTaskManager.getInstance(WXEntryActivity.this);
    private String code,name,unionid,headImg,gendle,access_token,openid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        IWXAPI api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                code = ((SendAuth.Resp)baseResp).code;
                if (code != null){
                    asyncTaskManager.request(GET_TOKEN,true,this);
                }

                break;
            default:
                Toast.makeText(WXEntryActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        switch (requestCode){
            case GET_TOKEN:
                try {

                    String login_url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+APP_ID+"&secret="+APP_SECRET+
                            "&code="+code+"&grant_type=authorization_code";
                    URL url = new URL(login_url);
                    return HotyiHttpConnection.getInstance(getApplicationContext()).getInfo(url);
                } catch (Exception e) {

                }
                break;
            case GET_INFO:
                try {

                    String login_url = "https://api.weixin.qq.com/sns/userinfo?"+"access_token="+access_token+"&openid="+openid;
                    URL url = new URL(login_url);
                    return HotyiHttpConnection.getInstance(getApplicationContext()).getInfo(url);
                } catch (Exception e) {

                }
                break;
        }
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {
            switch (requestCode){
                case GET_TOKEN:
                    try{
                        String resultInfo = result.toString();
                        Log.e("wechat info",resultInfo);
                        JSONObject  jsonObject = new JSONObject(resultInfo);
                        unionid = jsonObject.getString("openid");
                        access_token = jsonObject.getString("access_token");
                        openid = jsonObject.getString("openid");
                        asyncTaskManager.request(GET_INFO,true,this);
                    }catch (JSONException e){

                    }

                    break;
                case GET_INFO:
                    try{
                        String resultInfo = result.toString();
                        Log.e("wechat info info",resultInfo);
                        JSONObject jsonObject =  new JSONObject(resultInfo);
                        name = jsonObject.getString("nickname");
                        headImg = jsonObject.getString("headimgurl");
                        gendle = jsonObject.getString("sex");
                        Intent intent = new Intent(WXEntryActivity.this, LoginActivity.class);
                        intent.putExtra("name",name);
                        intent.putExtra("unionid",unionid);
                        intent.putExtra("gendle",gendle);
                        intent.putExtra("headImg",headImg);
                        startActivity(intent);
                        finish();
                    }catch (Exception e){

                    }
                    break;
            }
    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
}
