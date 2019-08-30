package com.zym.map.main;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.zym.map.base.ISelectAdrListener;
import com.zym.map.base.activity.MapBaseActivity;
import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.entity.LocationResult;
import com.zym.map.base.fragment.MapBaseFragment;
import com.zym.map.gao.de.FragmentGaoDeSelectAdr;
import com.zym.permission.PermissionsManager;
import com.zym.permission.PermissionsResultAction;
import com.zym.permission.dialog.ShowPermissionDialog;

public class ActivityMapMainIndex extends MapBaseActivity {

    //常量
    public static int KEY_REQUEST_CODE = 2001;
    public static int KEY_RESULT_CODE_SUCCESS = 2002;
    public static int KEY_RESULT_CODE_CANCEL = 2002;
    public static String KEY_RESULT_DATA = "mapMainResultData";

    private MapBaseFragment fmMain;

    private LinearLayout llMain;
    private TextView tvReturn,tvTitle;
    private ImageView ivSearch;
    private Button btnConfirm;

    private AddressDetail selectData;

    @Override
    public int getLayoutId() {
        return R.layout.map_main_index;
    }

    @Override
    public void createReady(@Nullable Bundle savedInstanceState) {
        initFragment();
        applyLocationPermission();
    }

    @Override
    protected void initView() {
        llMain = findViewById(R.id.ll_main);
        tvReturn = findViewById(R.id.tv_return);
        tvTitle = findViewById(R.id.tv_title);
        ivSearch = findViewById(R.id.iv_search);
        btnConfirm = findViewById(R.id.btn_confirm);

        //开始自定将ui
        if(customize != null){
            //背景
            if(customize.getBgColor() != 0){
                llMain.setBackgroundColor(customize.getBgColor());
            }

            //返回
            if(!TextUtils.isEmpty(customize.getBackText())){
                tvReturn.setText(customize.getBackText());
            }
            if (customize.getBackColor() != 0){
                tvReturn.setTextColor(customize.getBackColor());
            }

            //标题
            if(!TextUtils.isEmpty(customize.getTitle())){
                tvTitle.setText(customize.getTitle());
            }
            if(customize.getTitleColor() != 0){
                tvTitle.setTextColor(customize.getTitleColor());
            }

            //搜索按钮
            if (customize.getSearchIcon() != 0){
                ivSearch.setImageResource(customize.getSearchIcon());
            }

            //确定按钮
            if(!TextUtils.isEmpty(customize.getConfirmText())){
                btnConfirm.setText(customize.getConfirmText());
            }
            if (customize.getConfirmTextColor() != 0){
                btnConfirm.setTextColor(customize.getConfirmTextColor());
            }
            if(customize.getConfirmBg() != 0){
                btnConfirm.setBackgroundResource(customize.getConfirmBg());
            }
        }
    }

    @Override
    protected void initEvent() {
        ivSearch.setOnClickListener(v -> {
            Intent intent = new Intent(ActivityMapMainIndex.this, ActivityMapMainSearch.class);
            intent.putExtra(KEY_CUSTOMIZE_UI, customize);
            Bundle bundle = new Bundle();
            bundle.putString(ActivityMapMainSearch.SEARCH_CITY_KEY, fmMain.getLocationCity());
            //bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_TENCENT);
            bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_GAO_DE);
            //bundle.putInt(ActivityMapMainSearch.DATA_SOURCE_KEY, ActivityMapMainSearch.DATA_SOURCE_BAI_DU);

            intent.putExtras(bundle);
            startActivityForResult(intent, ActivityMapMainSearch.REQUEST_CODE);
        });
        tvReturn.setOnClickListener(v ->{
            setResult(KEY_RESULT_CODE_CANCEL);
            finish();
        });
        btnConfirm.setOnClickListener(v -> {
            if(selectData == null){
                return;
            }
            Intent data = new Intent();
            data.putExtra(KEY_RESULT_DATA, selectData);
            setResult(KEY_RESULT_CODE_SUCCESS, data);
            finish();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ActivityMapMainSearch.REQUEST_CODE){
            //搜索请求
            if(resultCode != ActivityMapMainSearch.RESULT_CODE_SUCCESS || data == null ){
                return;
            }
            //获取选择到的地址信息
            AddressDetail adSelect = data.getParcelableExtra(ActivityMapMainSearch.RESULT_DATA_KEY);
            //将地图移动到选择的地址位置
            fmMain.moveLocation(adSelect.getLat(), adSelect.getLng());
        }
    }

    /**
     * 申请定位权限
     */
    private void applyLocationPermission(){
        String[] permission = new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(this, permission, new PermissionsResultAction() {
            @Override
            public void onGranted() {
                fmMain.location();
            }

            @Override
            public void onDenied(String permission) {
                ShowPermissionDialog.newInstance(ActivityMapMainIndex.this, permission);
            }
        });
    }

    /**
     * 初始化 Fragment
     */
    private void initFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //fmMain = new FragmentTencentSelectAdr();
        fmMain = new FragmentGaoDeSelectAdr();
        //fmMain = new FragmentBaiDuSelectAdr();
        fmMain.setListener(new ISelectAdrListener() {
            @Override
            public void locationResult(LocationResult lr) {
                //定位完成
            }

            @Override
            public void clickItem(AddressDetail ad) {
                //item点击事件
                selectData = ad;
            }

            /**
             * 初始化完成 onCreateView 方法
             */
            @Override
            public void createReady() {
                if(customize != null){
                    //中间定位图标
                    if(customize.getLocationCenterIcon() != 0){
                        fmMain.setLocationCenterIcon(customize.getLocationCenterIcon());
                    }

                    //右下角定位图标
                    if (customize.getLocationBackIcon() != 0){
                        fmMain.setLocationBackIcon(customize.getLocationBackIcon());
                    }

                    //列表选中显示的图标
                    if (customize.getRvSelectIcon() != 0){
                        fmMain.setRvSelectIcon(customize.getRvSelectIcon());
                    }
                }
            }
        });
        ft.add(R.id.fl_fragment, fmMain);
        ft.show(fmMain);
        ft.commit();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_bottom_out);
    }
}
