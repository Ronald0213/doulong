package com.hotyi.hotyi.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.hotyi.hotyi.utils.async.OKHttpUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
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

//    private static final String ASSETS_PATH = "assets";
//    private final DefaultHttpClient httpClient;
//    private final HttpContext httpContext;


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

//    public String postConnection(HashMap<String ,String > map, URL url) {
//        String result;
//        if (map == null)
//            return "请输入正确的账号密码";
//        if (url == null)
//            return "网络连接异常";
//        try {
//            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            Log.e("httpconnection","        0      "+url.toString());
//            conn.setConnectTimeout(5000); //设置连接超时为5秒
//            conn.setRequestMethod("POST"); //设定请求方式
//            Log.e("httpconnection","        1");
//            conn.setRequestProperty("Content-Type","application/x-java-serialized-object");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            Iterator iter = map.entrySet().iterator();
////            String postInfo = null;
//            while (iter.hasNext()) {
//                Map.Entry entry = (Map.Entry) iter.next();
//                Log.e("httpconnection","        3");
//                String key = entry.getKey().toString();
//                String val = entry.getValue().toString();
//                conn.setRequestProperty(key,val);
//                Log.e("httpconnection","        4      "+key+"   "+val);
//                Log.e("httpconnection","        5");
//            }
//            conn.connect(); //建立到远程对象的实际连接
//
//            Log.e("httpconnection","        6");
//            InputStream is =conn.getInputStream();
//            Log.e("httpconnection","        7");
//            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//            byte[]buffer =  new byte[1024];
//            Log.e("httpconnection","        8");
//            int len = 0;
//            while (-1 != (len = is.read(buffer))){
//                byteArrayOutputStream.write(buffer,0,len);
//                Log.e("httpconnection","        9");
//                byteArrayOutputStream.flush();
//            }
//            result = byteArrayOutputStream.toString("utf-8");
//            Log.e("JSON",result);
//
//            //判断是否正常响应数据
//            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                System.out.println("网络错误异常！!!!");
//                return "网络错误";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            Log.e("httpconnection","   "+e.toString());
//            return "数据获取异常";
//        }
//        if (result != null)
//            return result;
//        return "网络数据异常";
//    }
//    public String post(Context context, String url, HttpEntity entity, String contentType) throws HttpException {
//        return sendRequest(httpClient, httpContext, addEntityToRequestBase(new HttpPost(url), entity), contentType, context);
//    }
//
//    /**
//     * Puts a new request in queue as a new thread in pool to be executed
//     *
//     * @param client      HttpClient to be used for request, can differ in single requests
//     * @param httpContext HttpContext in which the request will be executed
//     * @param uriRequest  instance of HttpUriRequest, which means it must be of
//     *                    HttpDelete, HttpPost, HttpGet, HttpPut, etc.
//     * @param contentType MIME body type, for POST and PUT requests, may be null
//     * @param context     Context of Android application
//     * @return
//     * @throws HttpException
//     */
//    protected String sendRequest(DefaultHttpClient client, HttpContext httpContext, HttpUriRequest uriRequest, String contentType, Context context) throws HttpException {
//
//        String responseBody = "";
//
//        if (contentType != null) {
//            uriRequest.addHeader("Content-Type", contentType);
//        }
//
////    	//set cookie
////    	List<Cookie> list = cookieStore.getCookies();
////		if(list != null && list.size() > 0){
////			for(Cookie cookie : list){
//////				uriRequest.setHeader("Cookie", cookie.getValue()); 通用cookie
////				uriRequest.setHeader("rong_im_auth", cookie.getValue());
////			}
////		}
//
//
////        List<Cookie> list = cookieStore.getCookies();
////        StringBuilder s = new StringBuilder();
////        if (list != null && list.size() > 0) {
////            for (Cookie cookie : list) {
////                s.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
////            }
////            uriRequest.setHeader("Cookie", s.toString());
////        }
//
//        try {
//            //get the response from assets
//            URI uri = uriRequest.getURI();
////            NLog.e(tag, "url : " + uri.toString());
//
//            String scheme = uri.getScheme();
//            if (!TextUtils.isEmpty(scheme) && ASSETS_PATH.equals(scheme)) {
//                String fileName = uri.getAuthority();
//                InputStream intput = context.getAssets().open(fileName);
//                responseBody = inputSteamToString(intput);
////                NLog.e(tag, "responseBody : " + responseBody);
//                return responseBody;
//            }
//
//            //get the response from network
//            HttpEntity bufferEntity = null;
//            HttpResponse response = client.execute(uriRequest, httpContext);
//            HttpEntity entity = response.getEntity();
//            if (entity != null) {
//                bufferEntity = new BufferedHttpEntity(entity);
//                responseBody = EntityUtils.toString(bufferEntity, "utf-8");
////                NLog.e(tag, "responseBody : " + responseBody);
//            }
//
//            // get cookie to save local  获取 cookie 存入本地
////			Header[] headers = response.getHeaders("Set-Cookie");
////	        if (headers != null && headers.length > 0) {
////	        	for(int i=0;i<headers.length;i++) {
////	        		String cookie = headers[i].getValue();
////	        		BasicClientCookie newCookie = new BasicClientCookie("cookie"+i, cookie);
////	        		cookieStore.addCookie(newCookie);
////	        	}
////	        }
//
////            SaveCookies(response);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new HttpException(e);
//        }
//
//        return responseBody;
//    }
//
//    public static String inputSteamToString(InputStream in) throws IOException {
//        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
//        byte[] data = new byte[1024];
//        int count = -1;
//        while ((count = in.read(data, 0, 1024)) != -1) {
//            outStream.write(data, 0, count);
//        }
//        data = null;
//        return new String(outStream.toByteArray(), "utf-8");
//    }


//    /**
//     * POST请求操作
//     *
//     * @param userName
//     * @param userPass
//     */
//    public String loginByPost(String userName, String userPass) {
//
//        try {
//
//            // 请求的地址
//            String spec = "https://app.muops.com/AppLoginReg/Login";
//            // 根据地址创建URL对象
//            URL url = new URL(spec);
//            // 根据URL对象打开链接
//            HttpURLConnection urlConnection = (HttpURLConnection) url
//                    .openConnection();
//            // 设置请求的方式
//            urlConnection.setRequestMethod("POST");
//            // 设置请求的超时时间
//            urlConnection.setReadTimeout(5000);
//            urlConnection.setConnectTimeout(5000);
//            // 传递的数据
//            String data = "RSAText" + URLEncoder.encode("c2c18c853bc1a158&15549455585&[B@4e80f5e8&1503372991074", "UTF-8");
//            // 设置请求的头
//            urlConnection.setRequestProperty("Connection", "keep-alive");
//            // 设置请求的头
//            urlConnection.setRequestProperty("Content-Type",
//                    "application/x-www-form-urlencoded");
//            // 设置请求的头
//            urlConnection.setRequestProperty("Content-Length",
//                    String.valueOf(data.getBytes().length));
//            // 设置请求的头
//            urlConnection
//                    .setRequestProperty("User-Agent",
//                            "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:27.0) Gecko/20100101 Firefox/27.0");
//
//            urlConnection.setDoOutput(true); // 发送POST请求必须设置允许输出
//            urlConnection.setDoInput(true); // 发送POST请求必须设置允许输入
//            //setDoInput的默认值就是true
//            //获取输出流
//            OutputStream os = urlConnection.getOutputStream();
//            os.write(data.getBytes());
//            os.flush();
//            if (urlConnection.getResponseCode() == 200) {
//                // 获取响应的输入流对象
//                InputStream is = urlConnection.getInputStream();
//                // 创建字节输出流对象
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                // 定义读取的长度
//                int len = 0;
//                // 定义缓冲区
//                byte buffer[] = new byte[1024];
//                // 按照缓冲区的大小，循环读取
//                while ((len = is.read(buffer)) != -1) {
//                    // 根据读取的长度写入到os对象中
//                    baos.write(buffer, 0, len);
//                }
//                // 释放资源
//                is.close();
//                baos.close();
//                // 返回字符串
//                String result = baos.toString();
//                Log.e("post login",result);
//                return result;
//
//                // 通过runOnUiThread方法进行修改主线程的控件内容
////                LoginActivity.this.runOnUiThread(new Runnable() {
////                    @Override
////                    public void run() {
////                        // 在这里把返回的数据写在控件上 会出现什么情况尼
////                        tv_result.setText(result);
////                    }
////                });
//
//            } else {
//                System.out.println("链接失败.........");
//                return "异常";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "异常";
//        }
//    }

//
    public String getInfo(URL url) throws Exception{
        Log.e("okhttp get","  done 1");
//        final StringBuffer result = new StringBuffer();
        OkHttpClient client = OKHttpUtil.getInstancec().getOkHttpClient();
        Log.e("okhttp get","  done 2");
        final Request request = new Request.Builder()
                .url(url)
                .build();
        Log.e("okhttp get","  done 3");
        Response response = client.newCall(request).execute();
        return response.body().string();
//        new call
//        Call call = client.newCall(request);
//        //请求加入调度
//        call.enqueue(new Callback()
//        {
//            @Override
//            public void onFailure(Request request, IOException e)
//            {
//                Log.e("okhttp get","  done 4");
//            }
//
//            @Override
//            public void onResponse(final Response response) throws IOException
//            {
//                Log.e("okhttp get","  done 5");
//                    String str = response.body().toString();
//                result.append(str);
//            }
//        });
//        Log.e("okhttp get"," done 6");
//        if (result != null)
//            return result.toString();
//        return  null;
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
}
