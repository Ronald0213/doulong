package com.hotyi.hotyi.utils;

import java.util.HashMap;

/**
 * Created by HOTYI on 2017/9/13.
 */

public class InfoUtils {
    private static HashMap<String, String> infoHashMap = new HashMap<>();
    public static InfoUtils instance;

    public static InfoUtils getInstance(){
        if (instance == null)
            instance = new InfoUtils();
        return instance;

    }


    public HashMap<String, String> getInfoHashMap() {
        return infoHashMap;
    }

    public void setInfoHashMap(HashMap<String, String> infoHashMap) {
        this.infoHashMap = infoHashMap;
    }

    public static void addInfo(String rongId,String id){
        infoHashMap.put(rongId,id);
    }

}
