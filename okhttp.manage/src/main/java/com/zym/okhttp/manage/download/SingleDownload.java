package com.zym.okhttp.manage.download;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 单文件下载
 * Created by 60907 on 2017/11/17.
 */

public class SingleDownload {
    private static String TAG = "SingleDownload";
    private static OkHttpClient okHttpClient=new OkHttpClient();
    private static Handler handler = new android.os.Handler(Looper.getMainLooper());
    public static void downLoadFile(String URL, final String saveDir, String fileName, final DownloadCallback callBack){
        final File file = new File(saveDir, fileName);
        if (file.exists()) {
            boolean delResult = file.delete();
            if(!delResult){
                callBack.error(null, "文件删除失败,无法下载");
                return;
            }
        }
        final Request request = new Request.Builder().url(URL).build();
        final Call call = okHttpClient.newCall(request);
        callBack.onBefore(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.error(null, e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if(response.code() != 200){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.error(null, "返回状态码不是200，是" + response.code());
                        }
                    });
                    return;
                }
                InputStream is = null;
                byte[] buf = new byte[2048];
                int len = 0;
                FileOutputStream fos = null;
                try {
                    final long total = response.body().contentLength();
                    Log.e(TAG, "total------>" + total);
                    long current = 0;
                    is = response.body().byteStream();
                    fos = new FileOutputStream(file);
                    while ((len = is.read(buf)) != -1) {
                        current += len;
                        fos.write(buf, 0, len);
                        Log.e(TAG, "current------>" + current);
                        int progress = (int) (current * 1.0f / total * 100);
                        setProgress(callBack, progress, total);
                    }
                    fos.flush();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.success(file);
                        }
                    });
                } catch (final IOException e) {
                    Log.e(TAG, e.toString());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            callBack.error(null, e.getMessage());
                        }
                    });
                } finally {
                    try {
                        if (is != null) {
                            is.close();
                        }
                        if (fos != null) {
                            fos.close();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        });
    }
    private static void setProgress(final DownloadCallback callBack, final long progress, final long total){
        handler.post(new Runnable() {
            @Override
            public void run() {
                callBack.onProgress(progress, total);
            }
        });
    }
    public interface DownloadCallback{
        void success(File file);
        void error(File file, String msg);
        void onProgress(long progress, long total);
        void onBefore(Request request);
    }
}
