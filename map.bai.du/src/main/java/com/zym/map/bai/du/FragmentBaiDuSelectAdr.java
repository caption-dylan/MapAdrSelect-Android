package com.zym.map.bai.du;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.LogoPosition;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.zym.json.convert.GsonConvert;
import com.zym.map.base.IMapConfig;
import com.zym.map.base.ISelectAdrListener;
import com.zym.map.base.assist.ConfigUtils;
import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.entity.BaiDuSurroundingSearch;
import com.zym.map.base.entity.LocationResult;
import com.zym.map.base.fragment.MapBaseFragment;
import com.zym.okhttp.manage.request.HttpRequest;
import com.zym.okhttp.manage.request.HttpRequestCallback;
import com.zym.okhttp.manage.request.ResponseData;

import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Response;

public class FragmentBaiDuSelectAdr extends MapBaseFragment implements View.OnClickListener {

    private MapView mMapView;
    private BaiduMap bMap;

    /**
     * 地图变化原因
     * REASON_GESTURE = 1                   用户手势触发导致的地图状态改变,比如双击、拖拽、滑动底图
     * REASON_API_ANIMATION = 2             SDK导致的地图状态改变, 比如点击缩放控件、指南针图标
     * REASON_DEVELOPER_ANIMATION = 3;      开发者调用,导致的地图状态改变
     */
    private int changeStartReason;

    //定位相关
    private LocationClient locationClient;
    private LocationClientOption locationOption;

    //当前定位的经纬度
    private LatLng currentLatLng;
    //选择的经纬度
    private LatLng selectLatLng;

    //记录当前在第几页
    private int pageIndex = 0;

    //移动来源， 0内部移动、1外部调用
    private int moveSource = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(getActivity().getApplicationContext());
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);
        mView = inflater.inflate(R.layout.map_bai_du_select_adr, container, false);
        initView();
        initEvent();
        initRecyclerView();
        listener.createReady();
        return mView;
    }

    /**
     * 初始化 view
     */
    private void initView(){
        mMapView = mView.findViewById(R.id.map_bai_du);
        mMapView.setLogoPosition(LogoPosition.logoPostionRightBottom);
        bMap = mMapView.getMap();
        //开启地图的定位图层
        bMap.setMyLocationEnabled(true);
        bMap.animateMapStatus(MapStatusUpdateFactory.zoomTo(15));
        UiSettings mUiSettings = bMap.getUiSettings();
        //通过设置enable为true或false 选择是否显示指南针
        mUiSettings.setCompassEnabled(false);
        //通过设置enable为true或false 选择是否显示比例尺
        mMapView.showScaleControl(false);
        //通过设置enable为true或false 选择是否显示缩放按钮
        mMapView.showZoomControls(false);

        mRv = mView.findViewById(R.id.rv_adr);
        ivBackCurrent = mView.findViewById(R.id.iv_back_current);
        ivMoveSelect = mView.findViewById(R.id.iv_move_select);
    }

    /**
     * 初始化事件监听
     */
    private void initEvent(){
        //回到中心点事件
        ivBackCurrent.setOnClickListener(this);
        //滚动事件监听
        mRv.setOnScrollChangeListener((View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) -> {
            /*
             *使用RecyclerView.canScrollVertically(1)，当返回值是false的时候，代表你的RecyclerView不能继续往下滚动啦，也就是说已经滚动到底部了
             * RecyclerView.canScrollVertically(-1)返回false的时候代表RecyclerView不能继续网上滚动了，已经到顶部了
             */
            if(!mRv.canScrollVertically(1)){
                //获取页数加一
                pageIndex ++;
                //获取数据
                getAddress();
            }
        });
        bMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                changeStartReason = i;
                Log.v("com.zym.map.bai.du", String.format("changeStartReason-->%d", changeStartReason));
            }

            @Override
            public void onMapStatusChange(MapStatus mapStatus) {

            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.v("com.zym.map.tencent", String.format("结束,isClick-->%b，initStatus-->%b", isClick, initStatus));
                //只有在非点击下面地址时才重新获取地址数据，如果是直接点击的，则不重新获取获取数据
                selectLatLng = mapStatus.target;
                if(!isClick && initStatus){
                    pageIndex = 0;
                    adAddress.setSelectIndex(0);
                    getAddress();
                }
                isClick = false;
            }
        });
    }

    /**
     * 根据经纬度获取附近地址
     */
    private void getAddress(){
        StringBuilder sb = new StringBuilder();
        sb.append("http://api.map.baidu.com/place/v2/search");
        sb.append(String.format(Locale.CANADA, "?location=%f,%f", selectLatLng.latitude, selectLatLng.longitude));
        sb.append("&query=房地产$美食$酒店$购物$生活服务$旅游景点$公司企业$休闲娱乐$运动健身$教育培训");
        sb.append("&output=json");
        sb.append("&coord_type=2");
        sb.append(String.format(Locale.CANADA,"&page_size=%d", IMapConfig.PAGE_SIZE));
        sb.append(String.format(Locale.CANADA,"&page_num=%d", pageIndex));
        sb.append(String.format(Locale.CANADA, "&ak=%s", ConfigUtils.getMetaData(getContext(), "com.baidu.lbsapi.web.API_KEY")));
        Log.v("com.zym.map.bai.du", String.format("url地址：%s", sb.toString()));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.bai.du", String.format("成功结果：%s", response));
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
                if(pageIndex == 0 && listAddress.size() > 0){
                    adAddress.setSelectIndex(0);
                    itemClick(0);
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.bai.du", String.format("失败结果：%s", response));
            }

            @Override
            public void onFinish() {

            }
        });
    }

    /***
     * 定位方法
     */
    @Override
    public void location() {
        if(mMapView == null){
            return;
        }
        if(locationClient == null){
            //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
            locationClient = new LocationClient(getContext());
            locationClient.registerLocationListener(new BDAbstractLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation bdLocation) {
                    //mapView 销毁后不在处理新接收的位置
                    if (bdLocation == null || mMapView == null){
                        Log.v("com.zym.map.bai.du", "定位失败");
                        return;
                    }
                    Log.v("com.zym.map.bai.du", "定位成功");
                    LocationResult lr;
                    //获取当前定位到的城市
                    locationCity = bdLocation.getCity();

                    //获取经纬度
                    double lat = bdLocation.getLatitude();
                    double lng = bdLocation.getLongitude();
                    currentLatLng = new LatLng(lat, lng);

                    if(!initStatus){
                        moveLocationInternal(lat, lng);
                        initStatus = true;
                        selectLatLng = currentLatLng;
                        pageIndex = 0;
                    }

                    MyLocationData locData = new MyLocationData.Builder()
                            .accuracy(bdLocation.getRadius())
                            // 此处设置开发者获取到的方向信息，顺时针0-360
                            .direction(bdLocation.getDirection())
                            .latitude(lat)
                            .longitude(lng)
                            .build();
                    bMap.setMyLocationData(locData);

                    lr = new LocationResult();
                    lr.setCode(0);
                    lr.setMsg("ok");
                    lr.setLat(lat);
                    lr.setLng(lng);
                    lr.setAddress(bdLocation.getAddress().address);
                    listener.locationResult(lr);
                }
            });
        }
        if (locationOption == null){
            //声明LocationClient类实例并配置定位参数
            locationOption = new LocationClientOption();
            //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
            locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
            //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
            locationOption.setCoorType("gcj02");
            //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
            locationOption.setScanSpan(30 * 1000);
            //可选，设置是否需要地址信息，默认不需要
            locationOption.setIsNeedAddress(true);
            //可选，设置是否需要地址描述
            locationOption.setIsNeedLocationDescribe(true);
            //可选，设置是否需要设备方向结果
            locationOption.setNeedDeviceDirect(false);
            //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
            locationOption.setLocationNotify(true);
            //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
            locationOption.setIgnoreKillProcess(true);
            //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
            locationOption.setIsNeedLocationDescribe(true);
            //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
            locationOption.setIsNeedLocationPoiList(true);
            //可选，默认false，设置是否收集CRASH信息，默认收集
            locationOption.SetIgnoreCacheException(false);
            //可选，默认false，设置是否开启Gps定位
            locationOption.setOpenGps(true);
            //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
            locationOption.setIsNeedAltitude(false);
            //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
            locationOption.setOpenAutoNotifyMode();
            //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
            locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        }
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    /**
     * 移动选点到指定位置
     *
     * @param lat 纬度
     * @param lng 经度
     */
    @Override
    public void moveLocation(double lat, double lng) {
        if(bMap == null){
            return;
        }
        LatLng latLng = new LatLng(lat, lng);
        if(moveSource == 1){
            selectLatLng = latLng;
            pageIndex = 0;
            adAddress.setSelectIndex(0);
            getAddress();
        }
        MapStatus mMapStatus = new MapStatus.Builder()
                .target(latLng)
                .build();
        //定义MapStatusUpdate对象，以便描述地图状态将要发生的变化
        MapStatusUpdate mMapStatusUpdate = MapStatusUpdateFactory.newMapStatus(mMapStatus);
        //改变地图状态
        bMap.animateMapStatus(mMapStatusUpdate);
    }

    /**
     * 移动选点到指定位置,内部调用
     *
     * @param lat 纬度
     * @param lng 经度
     */
    private void moveLocationInternal(double lat, double lng){
        //设置为内部调用
        moveSource = 0;
        moveLocation(lat, lng);
        //移动完成后设置为默认状态，外部调用
        moveSource = 1;
    }

    /**
     * 设置监听
     *
     * @param listener ISelectAdrListener
     */
    @Override
    public void setListener(ISelectAdrListener listener) {
        this.listener = listener;
    }

    /**
     * 获取定位城市
     *
     * @return 城市名
     */
    @Override
    public String getLocationCity() {
        return locationCity;
    }

    /**
     * 设置中间定位图标
     *
     * @param res
     */
    @Override
    public void setLocationCenterIcon(int res) {
        ivMoveSelect.setImageResource(res);
    }

    /**
     * 设置右下角的回到当前图标
     *
     * @param res
     */
    @Override
    public void setLocationBackIcon(int res) {
        ivBackCurrent.setImageResource(res);
    }

    /**
     * 设置列表选中时显示的图标
     *
     * @param res
     */
    @Override
    public void setRvSelectIcon(int res) {
        adAddress.setSelectIcon(res);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.iv_back_current){
            //回到当前定位的位置
            if(currentLatLng == null){
                return;
            }
            adAddress.setSelectIndex(0);
            mRv.smoothScrollToPosition(0);

            moveLocationInternal(currentLatLng.latitude, currentLatLng.longitude);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mMapView != null){
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(mMapView != null){
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (locationClient != null){
            locationClient.stop();
        }
        if(bMap!=null){
            bMap.setMyLocationEnabled(false);
        }
        if(mMapView != null){

            mMapView.onDestroy();
            mMapView = null;
        }
    }
}
