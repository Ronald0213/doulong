package com.hotyi.hotyi.utils;

import android.content.Context;

import java.io.DataInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

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

    public String postConnection(HashMap<String ,String > map, URL url) {
        String result;
        if (map == null)
            return "请输入正确的账号密码";
        if (url == null)
            return "网络连接异常";
        try {
            //返回一个URLConnection对象，它表示到URL所引用的远程对象的连接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000); //设置连接超时为5秒
            conn.setRequestMethod("POST"); //设定请求方式
            Iterator iter = map.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                String key = entry.getKey().toString();
                String val = entry.getValue().toString();
                conn.addRequestProperty(key,val);
            }
            conn.connect(); //建立到远程对象的实际连接
            //返回打开连接读取的输入流
            DataInputStream dis = new DataInputStream(conn.getInputStream());
            result = dis.toString();

            //判断是否正常响应数据
            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                System.out.println("网络错误异常！!!!");
                return "网络错误";
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("这是异常！");
            return "数据获取异常";
        }
        if (result != null)
            return result;
        return "网络数据异常";
    }
}
