package com.zym.okhttp.manage.request;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by zymapp on 2016/9/26.
 */

public class RequestParams {
    protected final Headers.Builder headers = new Headers.Builder();
    private final List<Part> params = new ArrayList<>();
    private final List<Part> files = new ArrayList<>();

    private int tag = -1;
    public void setTag(int tag){
        this.tag = tag;
    }
    public int getTag(){
        return tag;
    }

    protected HttpCycleContext httpCycleContext;

    private String httpTaskKey;
    private RequestBody requestBody;
    private boolean urlEncoder;//是否进行URL编码
    protected CacheControl cacheControl;

    public RequestParams() {
        this(null);
    }
    public RequestParams(HttpCycleContext httpCycleContext) {
        this.httpCycleContext = httpCycleContext;
        init();
    }

    private void init() {
        headers.add("charset", "UTF-8");

        List<Part> commonParams = OkHttp.getInstance().getCommonParams();
        if (commonParams != null && commonParams.size() > 0){
            params.addAll(commonParams);
        }

        //添加公共header
        Headers commonHeaders = OkHttp.getInstance().getCommonHeaders();
        if ( commonHeaders != null && commonHeaders.size() > 0 ) {
            for (int i = 0; i < commonHeaders.size(); i++) {
                String key = commonHeaders.name(i);
                String value = commonHeaders.value(i);
                headers.add(key, value);
            }
        }
        if ( httpCycleContext != null ) {
            httpTaskKey = httpCycleContext.getHttpTaskKey();
        }
    }

    public String getHttpTaskKey() {
        return this.httpTaskKey;
    }

    //==================================params====================================

    /**
     * @param key
     * @param value
     */
    public void addFormDataPart(String key, String value) {
        if ( value == null ) {
            value = "";
        }

        Part part = new Part(key, value);
        if (!Utils.isEmpty(key) && !params.contains(part)) {
            params.add(part);
        }
    }

    public void addFormDataPart(String key, int value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, long value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, float value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, double value) {
        addFormDataPart(key, String.valueOf(value));
    }

    public void addFormDataPart(String key, boolean value) {
        addFormDataPart(key, String.valueOf(value));
    }

    /**
     * @param key
     * @param file
     */
    public void addFormDataPart(String key, File file) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        boolean isPng = file.getName().lastIndexOf("png") > 0 || file.getName().lastIndexOf("PNG") > 0;
        if (isPng) {
            addFormDataPart(key, file, "image/png; charset=UTF-8");
            return;
        }

        boolean isJpg = file.getName().lastIndexOf("jpg") > 0 || file.getName().lastIndexOf("JPG") > 0
                ||file.getName().lastIndexOf("jpeg") > 0 || file.getName().lastIndexOf("JPEG") > 0;
        if (isJpg) {
            addFormDataPart(key, file, "image/jpeg; charset=UTF-8");
            return;
        }

        if (!isPng && !isJpg) {
            addFormDataPart(key, new FileWrapper(file, null));
        }
    }

    public void addFormDataPart(String key, File file, String contentType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        MediaType mediaType = null;
        try {
            mediaType = MediaType.parse(contentType);
        } catch (Exception e){
            //进行输出
        }

        addFormDataPart(key, new FileWrapper(file, mediaType));
    }

    public void addFormDataPart(String key, File file, MediaType mediaType) {
        if (file == null || !file.exists() || file.length() == 0) {
            return;
        }

        addFormDataPart(key, new FileWrapper(file, mediaType));
    }


    public void addFormDataPartFiles(String key, List<File> files) {
        for (File file:files){
            if (file == null || !file.exists() || file.length() == 0) {
                continue;
            }
            addFormDataPart(key, file);
        }
    }

    public void addFormDataPart(String key, List<File> files, MediaType mediaType) {
        for (File file:files){
            if (file == null || !file.exists() || file.length() == 0) {
                continue;
            }
            addFormDataPart(key, new FileWrapper(file, mediaType));
        }
    }

    public void addFormDataPart(String key, FileWrapper fileWrapper) {
        if (!Utils.isEmpty(key) && fileWrapper != null) {
            File file = fileWrapper.getFile();
            if (file == null || !file.exists() || file.length() == 0) {
                return;
            }
            files.add(new Part(key, fileWrapper));
        }
    }

    public void addFormDataPart(String key, List<FileWrapper> fileWrappers) {
        for (FileWrapper fileWrapper:fileWrappers){
            addFormDataPart(key, fileWrapper);
        }
    }

    public void addFormDataParts(List<Part> params) {
        this.params.addAll(params);
    }

    //==================================header====================================
    public void addHeader(String line) {
        headers.add(line);
    }

    public void addHeader(String key, String value) {
        if ( value == null ) {
            value = "";
        }

        if (!TextUtils.isEmpty(key)) {
            headers.add(key, value);
        }
    }

    public void addHeader(String key, int value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, long value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, float value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, double value) {
        addHeader(key, String.valueOf(value));
    }

    public void addHeader(String key, boolean value) {
        addHeader(key, String.valueOf(value));
    }

    /**
     * URL编码，只对GET,DELETE,HEAD有效
     */
    public void urlEncoder() {
        urlEncoder = true;
    }

    public boolean isUrlEncoder() {
        return urlEncoder;
    }

    public void setCacheControl(CacheControl cacheControl) {
        this.cacheControl = cacheControl;
    }

    public void clear() {
        params.clear();
        files.clear();
    }


    public void setCustomRequestBody(RequestBody requestBody) {
        this.requestBody = requestBody;
    }

    public void setRequestBodyString(String string) {
        setRequestBody(MediaType.parse("text/plain; charset=utf-8"), string);
    }

    public void setRequestBody(String mediaType, String string) {
        setRequestBody(MediaType.parse(mediaType), string);
    }

    public void setRequestBody(MediaType mediaType, String string) {
        setCustomRequestBody(RequestBody.create(mediaType, string));
    }

    public List<Part> getFormParams() {
        return params;
    }

    protected RequestBody getRequestBody() {
        RequestBody body = null;
        if (requestBody != null) {
            body = requestBody;
        } else if (files.size() > 0) {
            boolean hasData = false;
            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            for (Part part:params){
                String key = part.getKey();
                String value = part.getValue();
                builder.addFormDataPart(key, value);
                hasData = true;
            }

            for (Part part:files){
                String key = part.getKey();
                FileWrapper file = part.getFileWrapper();
                if (file != null) {
                    hasData = true;
                    builder.addFormDataPart(key, file.getFileName(), RequestBody.create(file.getMediaType(), file.getFile()));
                }
            }
            if (hasData) {
                body = builder.build();
            }
        } else {
            FormBody.Builder builder = new FormBody.Builder();
            for (Part part:params){
                String key = part.getKey();
                String value = part.getValue();
                builder.add(key, value);
            }
            body = builder.build();
        }

        return body;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Part part:params){
            String key = part.getKey();
            String value = part.getValue();
            if (result.length() > 0)
                result.append("&");

            result.append(key);
            result.append("=");
            result.append(value);
        }

        for (Part part:files){
            String key = part.getKey();
            if (result.length() > 0)
                result.append("&");

            result.append(key);
            result.append("=");
            result.append("FILE");
        }

        return result.toString();
    }

    /****
     * 将提交参数转换为待签名的String
     * @return
     */
    public String toSignString(){
        StringBuilder result = new StringBuilder();
        for (Part part:params){
            String key = part.getKey();
            String value = part.getValue();
            if (result.length() > 0)
                result.append("&");

            result.append(key);
            result.append("=");
            result.append(value);
        }
        return result.toString();
    }
}
