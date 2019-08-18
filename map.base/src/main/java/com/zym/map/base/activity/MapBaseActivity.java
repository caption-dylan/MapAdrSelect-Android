package com.zym.map.base.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zym.map.base.entity.Customize;
import com.zym.permission.PermissionsManager;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class MapBaseActivity extends AppCompatActivity {

    /**
     * 自定将ui
     */
    public static String KEY_CUSTOMIZE_UI = "keyCustomizeUi";

    protected Customize customize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        //初始化参数
        initBundle();
        //状态栏透明设置
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        View decor = window.getDecorView();
        //设置深色
        decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        createReady(savedInstanceState);
        initView();
        initEvent();
    }

    /**
     * 获取布局id
     * @return
     */
    public abstract int getLayoutId();

    /**
     * 创建完成事件，即onCreate事件，作了公共处理
     * @param savedInstanceState
     */
    public abstract void createReady(@Nullable Bundle savedInstanceState);

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 初始化事件
     */
    protected abstract void initEvent();

    /**
     * 权限申请公共处理
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionsManager.getInstance().notifyPermissionsChange(permissions, grantResults);
    }

    /**
     * 初始化外部传入的参数
     */
    protected void initBundle(){
        Intent data = getIntent();
        if(data == null){
            return;
        }
        customize = data.getParcelableExtra(KEY_CUSTOMIZE_UI);
    }
}
