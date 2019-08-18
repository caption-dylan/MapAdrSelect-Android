package com.zym.map.select;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.zym.map.base.entity.AddressDetail;
import com.zym.map.main.ActivityMapMainIndex;
import com.zym.map.base.entity.Customize;
import com.zym.okhttp.manage.request.OkHttp;
import com.zym.okhttp.manage.request.OkHttpConfig;
import com.zym.okhttp.manage.request.Part;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;
import okhttp3.Interceptor;

import static com.zym.okhttp.manage.request.IConfig.REQ_TIMEOUT;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHttp();
        Button btnSelect = this.findViewById(R.id.btn_select);
        btnSelect.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ActivityMapMainIndex.class);

            //修改ui的属性
            Customize customize = new Customize();

            customize.setBgColor(getResourcesColor(R.color.colorAccent));

            customize.setBackText(getResourcesString(R.string.back_name));
            customize.setBackColor(getResourcesColor(R.color.colorPrimary));

            customize.setTitle(getResourcesString(R.string.title));
            customize.setTitleColor(getResourcesColor(R.color.colorPrimary));

            customize.setSearchIcon(R.mipmap.ic_search);

            customize.setConfirmBg(R.drawable.confirm_bg);
            customize.setConfirmText(getResourcesString(R.string.confirm));
            customize.setConfirmTextColor(getResourcesColor(R.color.colorPrimary));

            customize.setLocationCenterIcon(R.mipmap.location);

            customize.setLocationBackIcon(R.mipmap.location_back);

            customize.setRvSelectIcon(R.mipmap.select);

            customize.setSearchBackIcon(R.mipmap.back);
            customize.setSearchInputHint("请输入地址");

            //传入对象对可重新更改ui界面
            //intent.putExtra(ActivityMapMainIndex.KEY_CUSTOMIZE_UI, customize);

            startActivityForResult(intent, ActivityMapMainIndex.KEY_REQUEST_CODE);
        });
        tvResult = findViewById(R.id.tv_result);
    }

    /**
     * 网络请求使用的的 okhttp3 最好可以在 application 中进行初始化
     */
    private void initHttp(){
        // region 初始化网络请求
        List<Part> commonParams = new ArrayList<>();
        Headers commonHeaders = new Headers.Builder().build();
        List<Interceptor> interceptorList = new ArrayList<>();
        OkHttpConfig.Builder builder = new OkHttpConfig.Builder()
                .setCommenParams(commonParams)
                .setCommenHeaders(commonHeaders)
                .setTimeout(REQ_TIMEOUT)
                .setInterceptors(interceptorList)
                .setDebug(true);
        OkHttp.getInstance().init(builder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data == null){
            return;
        }
        if(requestCode == ActivityMapMainIndex.KEY_REQUEST_CODE && resultCode == ActivityMapMainIndex.KEY_RESULT_CODE_SUCCESS){
            AddressDetail ad = data.getParcelableExtra(ActivityMapMainIndex.KEY_RESULT_DATA);
            tvResult.setText(ad.getDetail());
        }
    }

    /**
     * 获取xml中的String
     * @param id
     * @return
     */
    private String getResourcesString(int id){
       return getResources().getString(id);
    }

    /**
     * 获取xml中的Color
     * @param id
     * @return
     */
    private int getResourcesColor(int id){
        return ContextCompat.getColor(this, id);
    }
}
