package com.zym.okhttp.manage.request;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by zymapp on 2016/9/26.
 */

public final class HttpRequest implements IConfig {
    public static void get(String url) {
        get(url, null, null);
    }

    public static void get(String url, RequestParams params) {
        get(url, params, null);
    }

    public static void get(String url, HttpRequestCallback callback) {
        get(url, null, callback);
    }

    /**
     * Get请求
     * @param url
     * @param params
     * @param callback
     */
    public static void get(String url, RequestParams params, HttpRequestCallback callback) {
        get(url, params, REQ_TIMEOUT, callback);
    }

    public static void get(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.GET, url, params, builder, callback);
    }

    public static void get(String url, RequestParams params, OkHttpClient.Builder builder,HttpRequestCallback callback) {
        executeRequest(Method.GET, url, params, builder, callback);
    }

    public static void post(String url) {
        post(url, null, null);
    }

    public static void post(String url, RequestParams params) {
        post(url, params, null);
    }

    public static void post(String url, HttpRequestCallback callback) {
        post(url, null, callback);
    }

    /**
     * Post请求
     * @param url
     * @param params
     * @param callback
     */
    public static void post(String url, RequestParams params, HttpRequestCallback callback) {
        post(url, params, IConfig.REQ_TIMEOUT, callback);
    }

    public static void post(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.POST, url, params, builder, callback);
    }

    public static void post(String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        executeRequest(Method.POST, url, params, builder, callback);
    }

    public static void put(String url) {
        put(url, null, null);
    }

    public static void put(String url, RequestParams params) {
        put(url, params, null);
    }

    public static void put(String url, HttpRequestCallback callback) {
        put(url, null, callback);
    }

    /**
     * put请求
     * @param url
     * @param params
     * @param callback
     */
    public static void put(String url, RequestParams params, HttpRequestCallback callback) {
        put(url, params, IConfig.REQ_TIMEOUT, callback);
    }

    public static void put(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.PUT, url, params, builder, callback);
    }

    public static void put(String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        executeRequest(Method.PUT, url, params, builder, callback);
    }

    public static void delete(String url) {
        delete(url, null, null);
    }

    public static void delete(String url, RequestParams params) {
        delete(url, params, null);
    }

    public static void delete(String url, HttpRequestCallback callback) {
        delete(url, null, callback);
    }

    /**
     * delete请求
     * @param url
     * @param params
     * @param callback
     */
    public static void delete(String url, RequestParams params, HttpRequestCallback callback) {
        delete(url, params, IConfig.REQ_TIMEOUT, callback);
    }

    public static void delete(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.DELETE, url, params, builder, callback);
    }

    public static void delete(String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        executeRequest(Method.DELETE, url, params, builder, callback);
    }

    public static void head(String url) {
        head(url, null, null);
    }

    public static void head(String url, RequestParams params) {
        head(url, params, null);
    }

    public static void head(String url, HttpRequestCallback callback) {
        head(url, null, callback);
    }

    /**
     * head请求
     * @param url
     * @param params
     * @param callback
     */
    public static void head(String url, RequestParams params, HttpRequestCallback callback) {
        head(url, params, IConfig.REQ_TIMEOUT, callback);
    }

    public static void head(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.HEAD, url, params, builder, callback);
    }

    public static void head(String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        executeRequest(Method.HEAD, url, params, builder, callback);
    }

    public static void patch(String url) {
        patch(url, null, null);
    }

    public static void patch(String url, RequestParams params) {
        patch(url, params, null);
    }

    public static void patch(String url, HttpRequestCallback callback) {
        patch(url, null, callback);
    }

    /**
     * patch请求
     * @param url
     * @param params
     * @param callback
     */
    public static void patch(String url, RequestParams params, HttpRequestCallback callback) {
        patch(url, params, IConfig.REQ_TIMEOUT, callback);
    }

    public static void patch(String url, RequestParams params, long timeout, HttpRequestCallback callback) {
        OkHttpClient.Builder builder = OkHttp.getInstance().getOkHttpClientBuilder();
        builder.readTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.connectTimeout(timeout, TimeUnit.MILLISECONDS);
        builder.writeTimeout(timeout, TimeUnit.MILLISECONDS);
        executeRequest(Method.PATCH, url, params, builder, callback);
    }

    public static void patch(String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        executeRequest(Method.PATCH, url, params, builder, callback);
    }

    /**
     * 取消请求
     * @param url
     */
    public static void cancel(String url) {
        if ( !Utils.isEmpty(url) ) {
            Call call = OkHttpCallManager.getInstance().getCall(url);
            if ( call != null ) {
                call.cancel();
            }

            OkHttpCallManager.getInstance().removeCall(url);
        }
    }


    private static void executeRequest(Method method, String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        if (!Utils.isEmpty(url)) {
            if(builder == null) {
                builder = OkHttp.getInstance().getOkHttpClientBuilder();
            }
            OkHttpTask task = new OkHttpTask(method, url, params, builder, callback);
            task.execute();
        }
    }
}
