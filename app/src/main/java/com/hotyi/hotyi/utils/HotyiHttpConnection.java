package com.hotyi.hotyi.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hotyi.hotyi.utils.async.OKHttpUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by HOTYI on 2017/8/18.
 */

public class HotyiHttpConnection {

    private static HotyiHttpConnection instance;

    public static HotyiHttpConnection getInstance(Context context) {
        if (instance == null) {
            synchronized (HotyiHttpConnection.class) {
                if (instance == null) {
                    instance = new HotyiHttpConnection();
                }
            }
        }
        return instance;
    }

    public String getInfo(URL url) throws Exception{
        Log.e("okhttp get","  done 1");
        OkHttpClient client = OKHttpUtil.getInstancec().getOkHttpClient();
        Log.e("okhttp get","  done 2");
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Log.e("okhttp get","  done 3");
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public String post(HashMap<String, String> map, URL url) throws Exception {
        OkHttpClient client = OKHttpUtil.getInstancec().getOkHttpClient();
        Iterator iter = map.entrySet().iterator();
        String key = null;
        String val = null;
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key = entry.getKey().toString();
            val = entry.getValue().toString();
            if (key!=null)
                 formEncodingBuilder.add(key,val);
            Log.e("okhttp", key + "  "+ val);
        }
        if (key != null) {
            String result = null;
            RequestBody body = formEncodingBuilder
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                Log.e("OKHTTP", "   aaaa   " + result);
                return result;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } else
            return "异常";
    }

    public String post(HashMap<String, String> map, URL url, File file) throws Exception {
        OkHttpClient client = OKHttpUtil.getInstancec().getOkHttpClient();
        Iterator iter = map.entrySet().iterator();
        String key = null;
        String val = null;
        FormEncodingBuilder formEncodingBuilder = new FormEncodingBuilder();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            key = entry.getKey().toString();
            val = entry.getValue().toString();
            if (key!=null)
                formEncodingBuilder.add(key,val);
            Log.e("okhttp", key + "  "+ val);
        }
        if (key != null) {
            String result = null;
            RequestBody body = formEncodingBuilder
                    .build();


            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                result = response.body().string();
                Log.e("OKHTTP", "   aaaa   " + result);
                return result;
            } else {
                throw new IOException("Unexpected code " + response);
            }
        } else
            return "异常";
    }
}
