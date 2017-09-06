package com.hotyi.hotyi.wxapi;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import com.hotyi.hotyi.R;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private final String APP_ID = "wx4ae76f1d5b08feb3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
//        IWXAPI api = WXAPIFactory.createWXAPI(this,APP_ID,true);
//        api.handleIntent(getIntent(),this);
        Log.e("wechat", "  done 1");
        finish();
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.e("wechat", "  done 2");
        switch (baseResp.errCode){
            case BaseResp.ErrCode.ERR_OK:
                Log.e("wechat", "  done 3");
                Toast.makeText(WXEntryActivity.this,"登录成功",Toast.LENGTH_SHORT).show();

                break;
            default:
                Log.e("wechat", "  done 4");
                Toast.makeText(WXEntryActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
