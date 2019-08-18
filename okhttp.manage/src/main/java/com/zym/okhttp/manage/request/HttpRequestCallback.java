package com.zym.okhttp.manage.request;

import okhttp3.Headers;
import okhttp3.Response;

/**
 * Created by zymapp on 2016/9/26.
 * 网络请求回调
 */

public abstract class HttpRequestCallback {

    public static final int ERROR_RESPONSE_UNKNOWN = 1003;
    public static final int TIME_OUT = -1;

    protected Headers headers;

    public void onStart() {}
    public void onResponse(Response httpResponse, String response, Headers headers) {}
    public void onResponse(String response, Headers headers) {}

    protected abstract void onSuccess(Headers headers, Response httpResponse, String response, int tag);
    public abstract void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag);
    public abstract void onFinish();


    public Headers getHeaders() {
        return headers;
    }
    protected void setResponseHeaders(Headers headers) {
        this.headers = headers;
    }
}
