package com.zym.map.gao.de;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.zym.map.base.IMapConfig;
import com.zym.map.base.ISelectAdrListener;
import com.zym.map.base.assist.ConfigUtils;
import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.entity.LocationResult;
import com.zym.map.base.fragment.MapBaseFragment;
import com.zym.okhttp.manage.request.HttpRequest;
import com.zym.okhttp.manage.request.HttpRequestCallback;
import com.zym.okhttp.manage.request.ResponseData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Response;

public class FragmentGaoDeSelectAdr extends MapBaseFragment implements View.OnClickListener, AMapLocationListener {

    private MapView mMapView;
    //初始化地图控制器对象
    private AMap aMap;
    //当前位置的标记点
    private Marker marker;

    //定位相关
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption = null;

    //当前定位的经纬度
    private LatLng currentLatLng;
    //选择的经纬度
    private LatLng selectLatLng;

    //记录当前在第几页
    private int pageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_gao_de_select_adr, container, false);
        initView(savedInstanceState);
        initEvent();
        initRecyclerView();
        listener.createReady();
        return mView;
    }

    /**
     * 初始化 view
     * @param savedInstanceState Bundle
     */
    private void initView(@Nullable Bundle savedInstanceState){
        mMapView = mView.findViewById(R.id.map_gao_de);
        mMapView.onCreate(savedInstanceState);
        mRv = mView.findViewById(R.id.rv_adr);
        ivBackCurrent = mView.findViewById(R.id.iv_back_current);
        ivMoveSelect = mView.findViewById(R.id.iv_move_select);
        aMap = mMapView.getMap();
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
        //实例化UiSettings类对象
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setLogoPosition(AMapOptions.LOGO_POSITION_BOTTOM_RIGHT);
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
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                Log.v("com.zym.map.tencent", String.format("结束,isClick-->%b，initStatus-->%b", isClick, initStatus));
                //只有在非点击下面地址时才重新获取地址数据，如果是直接点击的，则不重新获取获取数据
                selectLatLng = cameraPosition.target;
                if(!isClick && initStatus){
                    pageIndex = 1;
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
        sb.append("https://restapi.amap.com/v3/place/around");
        sb.append(String.format(Locale.CANADA, "?location=%f,%f", selectLatLng.latitude, selectLatLng.longitude));
        sb.append(String.format(Locale.CANADA,"&offset=%d", IMapConfig.PAGE_SIZE));
        sb.append(String.format(Locale.CANADA,"&page=%d", pageIndex));
        sb.append("&types=050000|070000|120000");
        sb.append(String.format(Locale.CANADA, "&key=%s", ConfigUtils.getMetaData(getContext(), "com.amap.api.apikey.web")));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.gao.de", String.format("成功结果：%s", response));
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
                    if(pageIndex == 1){
                        listAddress.clear();
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

                if(pageIndex == 1 && listAddress.size() > 0){
                    adAddress.setSelectIndex(0);
                    itemClick(0);
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.gao.de", String.format("失败结果：%s", response));
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
        if(mLocationClient == null){
            mLocationClient = new AMapLocationClient(getContext());
        }
        if (mLocationOption == null){
            //初始化定位参数
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(30 * 1000);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            //启动定位
            mLocationClient.startLocation();
        }
        //设置定位监听
        mLocationClient.setLocationListener(this);
        //设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
    }

    /**
     * 移动选点到指定位置
     *
     * @param lat 纬度
     * @param lng 经度
     */
    @Override
    public void moveLocation(double lat, double lng) {
        if(aMap == null){
            return;
        }
        aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
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
            moveLocation(currentLatLng.latitude, currentLatLng.longitude);
        }
    }

    /**
     * 显示当前位置
     * @param lat double
     * @param lng double
     */
    private void showThisLocation(double lat, double lng){
        //判断当前的标记点是否存在
        LatLng ll = new LatLng(lat, lng);
        if (marker == null){
            //不存在的情况下先创建
            marker = aMap.addMarker(
                    new MarkerOptions()
                            .position(ll)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_base_location_tag))
                            .anchor(0.5f, 0.5f)
            );
        }
        //修改标记点的位置到新的经纬度
        marker.setPosition(ll);
    }

    /**
     * 定位完成事件
     * @param aMapLocation AMapLocation
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation == null){
            return;
        }
        LocationResult lr;
        if (aMapLocation.getErrorCode() == 0){
            Log.v("com.zym.map.gao.de", "定位成功！");
            //获取当前定位到的城市
            locationCity = aMapLocation.getCity();
            double lat = aMapLocation.getLatitude();
            double lng = aMapLocation.getLongitude();
            currentLatLng = new LatLng(lat, lng);
            if(!initStatus){
                moveLocation(lat, lng);
                initStatus = true;
                selectLatLng = currentLatLng;
                pageIndex = 1;
            }
            showThisLocation(lat, lng);
            lr = new LocationResult();
            lr.setCode(0);
            lr.setMsg("ok");
            lr.setLat(lat);
            lr.setLng(lng);
            lr.setAddress(aMapLocation.getAddress());
            listener.locationResult(lr);
        }else{
            Log.v("com.zym.map.gao.de", String.format("定位失败！code->%d、msg->%s", aMapLocation.getErrorCode(), aMapLocation.getErrorInfo()));
            lr = new LocationResult();
            lr.setCode(aMapLocation.getErrorCode());
            lr.setMsg(aMapLocation.getErrorInfo());
            listener.locationResult(lr);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mMapView != null){
            //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
            mMapView.onSaveInstanceState(outState);
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
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mMapView != null){
            mMapView.onDestroy();
        }
        if(mLocationClient != null){
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
    }
}
