package com.zym.okhttp.manage.request;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.SocketTimeoutException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by zymapp on 2016/9/26.
 */

public class OkHttpTask implements Callback,IConfig {

    private Handler handler = new Handler(Looper.getMainLooper());
    public static final String DEFAULT_HTTP_TASK_KEY = "default_http_task_key";

    private String url;
    private RequestParams params;
    private HttpRequestCallback callback;
    private Headers headers;
    private String requestKey;
    private Method method;
    private OkHttpClient okHttpClient;

    public OkHttpTask(Method method, String url, RequestParams params, OkHttpClient.Builder builder, HttpRequestCallback callback) {
        this.method = method;
        this.url = url;
        this.callback = callback;
        if (params == null) {
            this.params = new RequestParams();
        } else {
            this.params = params;
        }
        this.requestKey = this.params.getHttpTaskKey();
        if (Utils.isEmpty(requestKey)) {
            requestKey = DEFAULT_HTTP_TASK_KEY;
        }
        //将请求的URL及参数组合成一个唯一请求
        HttpTaskHandler.getInstance().addTask(this.requestKey, this);
        okHttpClient = builder.build();
    }

    public String getUrl() {
        return url;
    }

    protected void execute() {
        if (params.headers != null) {
            headers = params.headers.build();
        }

        if (callback != null) {
            callback.onStart();
        }

        try {
            run();
        } catch (Exception e) {
            //进行输出
        }
    }

    protected void run() throws Exception{
        String srcUrl = url;
        //构建请求Request实例
        Request.Builder builder = new Request.Builder();

        switch (method) {
            case GET:
                url = Utils.getFullUrl(url, params.getFormParams(), params.isUrlEncoder());
                builder.get();
                break;
            case DELETE:
                url = Utils.getFullUrl(url, params.getFormParams(), params.isUrlEncoder());
                builder.delete();
                break;
            case HEAD:
                url = Utils.getFullUrl(url, params.getFormParams(), params.isUrlEncoder());
                builder.head();
                break;
            case POST:
                RequestBody body = params.getRequestBody();
                if (body != null) {
                    builder.post(new ProgressRequestBody(body));
                }
                break;
            case PUT:
                RequestBody bodyPut = params.getRequestBody();
                if (bodyPut != null) {
                    builder.put(new ProgressRequestBody(bodyPut));
                }
                break;
            case PATCH:
                RequestBody bodyPatch = params.getRequestBody();
                if (bodyPatch != null) {
                    builder.put(new ProgressRequestBody(bodyPatch));
                }
                break;
        }
        if (params.cacheControl != null) {
            builder.cacheControl(params.cacheControl);
        }
        builder.url(url).tag(srcUrl).headers(headers);
        Request request = builder.build();
        Call call = okHttpClient.newCall(request);
        OkHttpCallManager.getInstance().addCall(url, call);
        //执行请求
        call.enqueue(this);
    }


    @Override
    public void onFailure(Call call, IOException e) {
        ResponseData responseData = new ResponseData();
        if (e instanceof SocketTimeoutException) {
            responseData.setTimeout(true);
        } else if (e instanceof InterruptedIOException && TextUtils.equals(e.getMessage(), "timeout")) {
            responseData.setTimeout(true);
        }

        handlerResponse(responseData, null);
    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {
        ResponseData responseData = new ResponseData();
        handlerResponse(responseData, response);
    }

    private void handlerResponse(final ResponseData responseData, Response response) {
        //获取请求结果
        if (response != null) {
            responseData.setResponseNull(false);
            responseData.setCode(response.code());
            responseData.setMessage(response.message());
            responseData.setSuccess(response.isSuccessful());
            String respBody = "";
            try {
                respBody = response.body().string();
            } catch (IOException e) {
                //进行输出
            }
            responseData.setResponse(respBody);
            responseData.setHeaders(response.headers());
        } else {
            responseData.setResponseNull(true);
            responseData.setCode(HttpRequestCallback.ERROR_RESPONSE_UNKNOWN);
            if(responseData.isTimeout()) {
                responseData.setMessage("request timeout");
                responseData.setCode(HttpRequestCallback.TIME_OUT);
            } else {
                responseData.setMessage("http exception");
            }
        }
        responseData.setHttpResponse(response);

        //跳转到主线程
        handler.post(new Runnable() {
            @Override
            public void run() {
                onPostExecute(responseData);
            }
        });
    }

    protected void onPostExecute(ResponseData responseData) {
        OkHttpCallManager.getInstance().removeCall(url);
        //判断请求是否在这个集合中
        if (!HttpTaskHandler.getInstance().contains(requestKey)) {
            return;
        }

        if (callback != null) {
            callback.setResponseHeaders(responseData.getHeaders());
            callback.onResponse(responseData.getHttpResponse(), responseData.getResponse(), responseData.getHeaders());
            callback.onResponse(responseData.getResponse(), responseData.getHeaders());
        }

        if (!responseData.isResponseNull()) {//请求得到响应
            if (responseData.isSuccess()) {//成功的请求
                parseResponseBody(responseData, callback);
            } else {//请求失败
                if (callback != null) {
                    callback.onFailure(responseData, responseData.getHeaders(), responseData.getHttpResponse(), responseData.getResponse(), params.getTag());
                }
            }
        } else {
            if (callback != null) {
                callback.onFailure(responseData, responseData.getHeaders(), responseData.getHttpResponse(), responseData.getResponse(), params.getTag());
            }
        }

        if (callback != null) {
            callback.onFinish();
        }
    }

    /**
     * 解析响应数据
     *
     * @param responseData 请求的response
     * @param callback     请求回调
     */
    private void parseResponseBody(ResponseData responseData, HttpRequestCallback callback) {
        //回调为空，不向下执行
        if (callback == null) {
            return;
        }
        if(responseData.isSuccess()){
            callback.onSuccess(responseData.getHeaders(), responseData.getHttpResponse(), responseData.getResponse(), params.getTag());
        }else{
            //接口请求失败
            callback.onFailure(responseData, responseData.getHeaders(), responseData.getHttpResponse(), responseData.getResponse(), params.getTag());
        }
    }
}
