package com.zym.map.main;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zym.json.convert.GsonConvert;
import com.zym.map.base.IMapConfig;
import com.zym.map.base.activity.MapBaseActivity;
import com.zym.map.base.adapter.AdapterAddress;
import com.zym.map.base.assist.ConfigUtils;
import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.entity.AddressSearch;
import com.zym.map.base.entity.BaiDuSurroundingSearch;
import com.zym.okhttp.manage.request.HttpRequest;
import com.zym.okhttp.manage.request.HttpRequestCallback;
import com.zym.okhttp.manage.request.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Response;

public class ActivityMapMainSearch extends MapBaseActivity {

    /**
     * 用于获取和设置当前查询的城市
     */
    public static String SEARCH_CITY_KEY = "searchCity";
    /**
     * 用于获取和设置当前选择的地址信息
     */
    public static String RESULT_DATA_KEY = "resultDataKey";
    /**
     * 用于获取和设置当前数据来源
     */
    public static String DATA_SOURCE_KEY = "dataSourceKey";
    /**
     * activity 请求id
     */
    public static int REQUEST_CODE = 1;
    /**
     * activity 单纯关闭返回状态
     */
    public static int RESULT_CODE_CLOSE = 1;
    /**
     * activity 选择地址关闭返回状态
     */
    public static int RESULT_CODE_SUCCESS = 2;

    /**
     * 数据来源-腾讯
     */
    public static int DATA_SOURCE_TENCENT = 1;

    /**
     * 数据来源-高德
     */
    public static int DATA_SOURCE_GAO_DE = 2;

    /**
     * 数据来源-百度
     */
    public static int DATA_SOURCE_BAI_DU = 3;

    private ImageView ivReturn;
    private EditText etSearch;
    private RecyclerView mRv;

    //当前指定的城市
    private String searchCity;
    //当前查询关键字
    private String searchKey = "";
    //数据来源，默认使用腾讯的
    private int dataSource = 1;

    //外部传入参数对象
    private Bundle bundle;

    //记录当前第几页
    private int pageIndex = 1;

    //列表记录内容
    private List<AddressDetail> listAddress = new ArrayList<>();

    //列表数据源
    private AdapterAddress adAddress;

    /**
     * 获取布局id
     *
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.map_main_search;
    }

    /**
     * 创建完成事件，即onCreate事件，作了公共处理
     *
     * @param savedInstanceState
     */
    @Override
    public void createReady(@Nullable Bundle savedInstanceState) {
        mInitBundle();
    }

    /**
     * 初始化View
     */
    @Override
    protected void initView() {
        ivReturn = findViewById(R.id.iv_return);
        etSearch = findViewById(R.id.et_search);
        mRv = findViewById(R.id.rv_search);
        initRecyclerView();
        if(customize != null){
            if(customize.getSearchBackIcon() != 0){
                ivReturn.setImageResource(customize.getSearchBackIcon());
            }
            if(!TextUtils.isEmpty(customize.getSearchInputHint())){
                etSearch.setHint(customize.getSearchInputHint());
            }
        }
    }

    /**
     * 初始化事件
     */
    @Override
    protected void initEvent() {
        ivReturn.setOnClickListener(v -> {
            setResult(RESULT_CODE_CLOSE);
            finish();
        });
        mRv.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            /*
             *使用RecyclerView.canScrollVertically(1)，当返回值是false的时候，代表你的RecyclerView不能继续往下滚动啦，也就是说已经滚动到底部了
             * RecyclerView.canScrollVertically(-1)返回false的时候代表RecyclerView不能继续网上滚动了，已经到顶部了
             */
            if(!mRv.canScrollVertically(1)){
                //获取页数加一
                pageIndex ++;
                //获取数据
                getData(searchKey);
            }
        });
        //输入框输入监听
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                searchKey = s.toString();
                if (s.toString().length() <= 0){
                    pageIndex = 1;
                    listAddress.clear();
                    adAddress.notifyDataSetChanged();
                    return;
                }
                adAddress.setKeyword(searchKey);
                pageIndex = 1;
                listAddress.clear();
                mRv.smoothScrollToPosition(0);
                getData(searchKey);
            }
        });
    }

    /**
     * 初始化传入参数
     */
    private void mInitBundle(){
        bundle = getIntent().getExtras();
        if(bundle == null){
            Log.e("com.zym.map.main", "请输入[searchCity]参数");
            return;
        }
        searchCity = bundle.getString(SEARCH_CITY_KEY);
        if(TextUtils.isEmpty(searchCity)){
            Log.e("com.zym.map.main", "请输入[searchCity]参数");
        }
        dataSource = bundle.getInt(DATA_SOURCE_KEY);
        if(dataSource == 0){
            dataSource = 1;
        }
    }

    /**
     * 初始化 RecyclerView
     */
    private void initRecyclerView(){
        // 定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(this);
        // 设置布局管理器
        mRv.setLayoutManager(manager);
        adAddress = new AdapterAddress(ActivityMapMainSearch.this, listAddress, position -> {
            Intent data = new Intent();
            AddressDetail adSelect = listAddress.get(position);
            data.putExtra(RESULT_DATA_KEY, adSelect);
            setResult(RESULT_CODE_SUCCESS, data);
            finish();
        });
        mRv.setAdapter(adAddress);
        //设置启用字段关键字高亮
        adAddress.setHighlight(true);
    }

    /**
     * 获取数据
     */
    private void getData(String searchKey){
        if(dataSource == DATA_SOURCE_TENCENT){
            tencentSearch(searchKey);
        }else if (dataSource == DATA_SOURCE_GAO_DE){
            gaoDeSearch(searchKey);
        }else if (dataSource == DATA_SOURCE_BAI_DU){
            baiDuSearch(searchKey);
        }
    }

    /**
     * 腾讯关键字输入提示
     * @param searchKey
     */
    private void tencentSearch(String searchKey){
        StringBuilder sb = new StringBuilder();
        sb.append("https://apis.map.qq.com/ws/place/v1/suggestion");
        sb.append(String.format(Locale.CANADA, "?keyword=%s", searchKey));
        sb.append(String.format(Locale.CANADA, "&region=%s", searchCity));
        sb.append(String.format(Locale.CANADA, "&page_size=%d", IMapConfig.PAGE_SIZE));
        sb.append(String.format(Locale.CANADA, "&page_index=%d", pageIndex));
        sb.append(String.format(Locale.CANADA, "&key=%s", ConfigUtils.getMetaData(this, "TencentMapSDK")));
        Log.v("com.zym.map.tencent", String.format("url地址：%s", sb.toString()));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(okhttp3.Headers headers, okhttp3.Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("成功结果：%s", response));
                if(TextUtils.isEmpty(response)){
                    return;
                }
                AddressSearch tmpSearch = GsonConvert.fromJson(response, AddressSearch.class);
                if(tmpSearch.getStatus() != 0){
                    return;
                }
                if(pageIndex == 1){
                    listAddress.clear();
                }
                for (AddressSearch.Data item : tmpSearch.getData()) {
                    AddressDetail adTmp = new AddressDetail();
                    adTmp.setTitle(item.getTitle());
                    adTmp.setDetail(item.getAddress());
                    adTmp.setLat(item.getLocation().getLat());
                    adTmp.setLng(item.getLocation().getLng());
                    listAddress.add(adTmp);
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, okhttp3.Headers headers, okhttp3.Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("失败结果：%s", response));
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 高德关键字输入提示
     * @param searchKey
     */
    private void gaoDeSearch(String searchKey){
        StringBuilder sb = new StringBuilder();
        sb.append("https://restapi.amap.com/v3/place/text");
        sb.append(String.format(Locale.CANADA, "?keywords=%s", searchKey));
        sb.append(String.format(Locale.CANADA, "&city=%s", searchCity));
        sb.append(String.format(Locale.CANADA, "&offset=%d", IMapConfig.PAGE_SIZE));
        sb.append(String.format(Locale.CANADA, "&page=%d", pageIndex));
        sb.append("&types=010000|020000|030000|040000|050000|060000|070000|080000|090000|100000|110000|120000");
        sb.append(String.format(Locale.CANADA, "&key=%s", ConfigUtils.getMetaData(this, "com.amap.api.apikey.web")));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("成功结果：%s", response));
                try {
                    JSONObject all = new JSONObject(response);
                    String status = null;
                    if(all.has("status")){
                        status = all.getString("status");
                    }
                    if(!"1".equals(status)){
                        return;
                    }
                    JSONArray pois = null;
                    if(all.has("pois")){
                        pois = all.getJSONArray("pois");
                    }
                    if(pois == null){
                        return;
                    }
                    int length = pois.length();
                    for(int i = 0; i<length; i++){
                        AddressDetail tmpDetail = new AddressDetail();
                        JSONObject item = pois.getJSONObject(i);
                        tmpDetail.setTitle(ConfigUtils.getJsonValue(item, "name"));
                        tmpDetail.setDetail(ConfigUtils.getJsonValue(item, "address"));
                        String location = ConfigUtils.getJsonValue(item, "location");
                        String[] latLng = location.split(",");
                        tmpDetail.setLng(Double.valueOf(latLng[0]));
                        tmpDetail.setLat(Double.valueOf(latLng[1]));
                        listAddress.add(tmpDetail);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("失败结果：%s", response));
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /**
     * 百度关键字输入提示
     */
    private void baiDuSearch(String searchKey){
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/place/v2/search");
        sb.append(String.format(Locale.CANADA, "?query=%s", searchKey));
        sb.append(String.format(Locale.CANADA, "&region=%s", searchCity));
        sb.append(String.format(Locale.CANADA, "&page_size=%d", IMapConfig.PAGE_SIZE));
        sb.append(String.format(Locale.CANADA, "&page_num=%d", pageIndex));
        sb.append("&output=json");
        sb.append("&coord_type=2");
        sb.append(String.format(Locale.CANADA, "&ak=%s", ConfigUtils.getMetaData(this, "com.baidu.lbsapi.web.API_KEY")));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("成功结果：%s", response));
                if(TextUtils.isEmpty(response)){
                    return;
                }
                BaiDuSurroundingSearch bdss = GsonConvert.fromJson(response, BaiDuSurroundingSearch.class);
                if(bdss == null){
                    return;
                }
                if(bdss.getStatus() != 0){
                    return;
                }
                if(pageIndex == 0){
                    listAddress.clear();
                }
                for (BaiDuSurroundingSearch.Results item : bdss.getResults()) {
                    AddressDetail tmpDetail = new AddressDetail();
                    tmpDetail.setTitle(item.getName());
                    tmpDetail.setDetail(item.getAddress());
                    tmpDetail.setLng(item.getLocation().getLng());
                    tmpDetail.setLat(item.getLocation().getLat());
                    listAddress.add(tmpDetail);
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.main", String.format("失败结果：%s", response));
            }

            @Override
            public void onFinish() {

            }
        });
    }
}
