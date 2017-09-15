package com.hotyi.hotyi.activity;

import android.os.Bundle;
import android.app.Activity;

import com.hotyi.hotyi.R;
import com.hotyi.hotyi.utils.HttpException;
import com.hotyi.hotyi.utils.async.AsyncTaskManager;
import com.hotyi.hotyi.utils.async.OnDataListener;

public class MyBaseActivity extends BaseUiActivity  implements OnDataListener {

    public AsyncTaskManager mAsyncTaskManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAsyncTaskManager = AsyncTaskManager.getInstance(getApplicationContext());

    }

    public void request(int requestCode) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, this);
        }
    }

    /**
     * 发送请求（需要检查网络）
     *
     * @param id 请求数据的用户ID或者groupID
     * @param requestCode 请求码
     */
    public void request(String id , int requestCode) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(id, requestCode, this);
        }
    }

    /**
     * 发送请求
     *
     * @param requestCode    请求码
     * @param isCheckNetwork 是否需检查网络，true检查，false不检查
     */
    public void request(int requestCode, boolean isCheckNetwork) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, isCheckNetwork, this);
        }
    }

    /**
     * 取消所有请求
     */
    public void cancelRequest() {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.cancelRequest();
        }
    }


    @Override
    public Object doInBackground(int requestCode, String parameter) throws HttpException {
        return null;
    }

    @Override
    public void onSuccess(int requestCode, Object result) {

    }

    @Override
    public void onFailure(int requestCode, int state, Object result) {

    }
}
