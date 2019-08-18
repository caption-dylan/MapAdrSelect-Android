package com.zym.okhttp.manage.request;

import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;

/**
 * Created by zymapp on 2016/9/26.
 */

public class OkHttpCallManager {
    private ConcurrentHashMap<String, Call> callMap;
    private static OkHttpCallManager manager;

    private OkHttpCallManager() {
        callMap = new ConcurrentHashMap<>();
    }

    public static OkHttpCallManager getInstance() {
        if (manager == null) {
            manager = new OkHttpCallManager();
        }
        return manager;
    }

    public void addCall(String url, Call call) {
        if (call != null && !Utils.isEmpty(url)) {
            callMap.put(url, call);
        }
    }

    public Call getCall(String url) {
        if ( !Utils.isEmpty(url) ) {
            return callMap.get(url);
        }

        return null;
    }

    public void removeCall(String url) {
        if ( !Utils.isEmpty(url) ) {
            callMap.remove(url);
        }
    }
}
