package com.zym.map.base.assist;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.json.JSONException;
import org.json.JSONObject;

/***
 * 设置与读取配置相关
 */
public class ConfigUtils {
    public static String getMetaData(Context context, String key){
        String metaStr = "";
        try {
            ApplicationInfo applicationInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (applicationInfo != null && applicationInfo.metaData != null) {
                // 这里为对应meta-data的name
                metaStr = (String) applicationInfo.metaData.get(key);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return metaStr;
    }

    /**
     * 获取json中的值
     * @param obj JSONObject
     * @param key String
     * @return String
     */
    public static String getJsonValue(JSONObject obj, String key){
        String value = "";
        if(obj.has(key)){
            try {
                value = obj.getString(key);
            } catch (JSONException e) {
                value = "";
            }
            if(value == null){
                value = "";
            }
        }
        return value;
    }
}
