package com.zym.map.tencent;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.reflect.TypeToken;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.map.geolocation.TencentLocationManager;
import com.tencent.map.geolocation.TencentLocationRequest;
import com.tencent.tencentmap.mapsdk.maps.CameraUpdateFactory;
import com.tencent.tencentmap.mapsdk.maps.MapView;
import com.tencent.tencentmap.mapsdk.maps.TencentMap;
import com.tencent.tencentmap.mapsdk.maps.model.BitmapDescriptorFactory;
import com.tencent.tencentmap.mapsdk.maps.model.CameraPosition;
import com.tencent.tencentmap.mapsdk.maps.model.LatLng;
import com.tencent.tencentmap.mapsdk.maps.model.Marker;
import com.tencent.tencentmap.mapsdk.maps.model.MarkerOptions;
import com.zym.json.convert.GsonConvert;
import com.zym.map.base.IMapConfig;
import com.zym.map.base.assist.ConfigUtils;
import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.ISelectAdrListener;
import com.zym.map.base.entity.LocationResult;
import com.zym.map.base.entity.ReverseAddress;
import com.zym.map.base.fragment.MapBaseFragment;
import com.zym.okhttp.manage.request.HttpRequest;
import com.zym.okhttp.manage.request.HttpRequestCallback;
import com.zym.okhttp.manage.request.ResponseData;

import java.util.Locale;

import okhttp3.Headers;
import okhttp3.Response;

public class FragmentTencentSelectAdr extends MapBaseFragment implements TencentLocationListener, View.OnClickListener {

    private MapView mMapView;
    private TencentMap tencentMap;

    //当前位置的标记点
    private Marker marker;

    //定位相关
    private TencentLocationRequest request;
    private TencentLocationManager locationManager;

    //当前定位的经纬度
    private LatLng currentLatLng;
    //选择的经纬度
    private LatLng selectLatLng;

    //记录当前在第几页
    private int pageIndex = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.map_tencent_select_adr, container, false);
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
        mMapView = mView.findViewById(R.id.map_tencent);
        mRv = mView.findViewById(R.id.rv_adr);
        ivBackCurrent = mView.findViewById(R.id.iv_back_current);
        ivMoveSelect = mView.findViewById(R.id.iv_move_select);
        tencentMap = mMapView.getMap();
    }

    /**
     * 初始化事件监听
     */
    private void initEvent(){
        //回到中心点事件
        ivBackCurrent.setOnClickListener(this);
        //地图拖动变化事件
        tencentMap.setOnCameraChangeListener(new TencentMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
            }

            @Override
            public void onCameraChangeFinished(CameraPosition cameraPosition) {
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
    }

    /**
     * 根据经纬度获取附近地址
     */
    private void getAddress(){
        StringBuilder sb = new StringBuilder();
        sb.append("https://apis.map.qq.com/ws/geocoder/v1/");
        sb.append(String.format(Locale.CHINESE,"?location=%f,%f", selectLatLng.latitude, selectLatLng.longitude));
        sb.append("&get_poi=1");
        sb.append(String.format(Locale.CANADA, "&poi_options=radius=5000;page_size=%d;page_index=%d;policy=2", IMapConfig.PAGE_SIZE, pageIndex));
        sb.append(String.format(Locale.CANADA, "&key=%s", ConfigUtils.getMetaData(getContext(), "TencentMapSDK")));
        Log.v("com.zym.map.tencent", String.format("url地址：%s", sb.toString()));
        HttpRequest.get(sb.toString(), new HttpRequestCallback() {
            @Override
            protected void onSuccess(okhttp3.Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.tencent", String.format("成功结果：%s", response));
                if(TextUtils.isEmpty(response)){
                    return;
                }
                ReverseAddress tmp = GsonConvert.fromJson(response, new TypeToken<ReverseAddress>(){});
                if(tmp == null){
                    return;
                }
                if(tmp.getStatus() != 0){
                    return;
                }
                if(pageIndex == 1){
                    listAddress.clear();
                }

                for (ReverseAddress.Pois item: tmp.getResult().getPois()) {
                    AddressDetail tmpDetail = new AddressDetail();
                    tmpDetail.setTitle(item.getTitle());
                    tmpDetail.setDetail(item.getAddress());
                    tmpDetail.setLat(item.getLocation().getLat());
                    tmpDetail.setLng(item.getLocation().getLng());
                    listAddress.add(tmpDetail);
                }
                if(pageIndex == 1 && listAddress.size() > 0){
                    adAddress.setSelectIndex(0);
                    itemClick(0);
                }
                adAddress.notifyDataSetChanged();
            }

            @Override
            public void onFailure(ResponseData rd, Headers headers, Response httpResponse, String response, int tag) {
                Log.v("com.zym.map.tencent", String.format("失败结果：%s", response));
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
        if(request == null){
            request = TencentLocationRequest.create();
            request.setRequestLevel(TencentLocationRequest.REQUEST_LEVEL_ADMIN_AREA);
        }
        if(locationManager == null){
            locationManager = TencentLocationManager.getInstance(getActivity());
        }
        int status = locationManager.requestLocationUpdates(request, this);
        if (status == 0) {
            Log.v("com.zym.map.tencent", "注册位置监听器成功！");
        } else {
            Log.v("com.zym.map.tencent", String.format("注册位置监听器失败！失败码：%d", status));
        }
    }

    /**
     * 移动选点到指定位置
     * @param lat 纬度
     * @param lng 经度
     */
    @Override
    public void moveLocation(double lat, double lng){
        if(tencentMap == null){
            return;
        }
        tencentMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(lat, lng)));
    }

    /**
     * 设置监听
     * @param listener ISelectAdrListener
     */
    @Override
    public void setListener(ISelectAdrListener listener){
        this.listener = listener;
    }

    /**
     * 获取定位的城市
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
     * 显示当前位置
     * @param location TencentLocation
     */
    private void showThisLocation(TencentLocation location){
        //创建经纬度
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        //判断当前的标记点是否存在
        if (marker == null){
            //不存在的情况下先创建
            marker = tencentMap.addMarker(
                    new MarkerOptions(latLng)
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.map_base_location_tag))
                    .anchor(0.5f, 0.5f)
            );
        }
        //修改标记点的位置到新的经纬度
        marker.setPosition(latLng);
        //仅当定位来源于gps有效，或者使用方向传感器
        marker.setRotation(location.getBearing());
    }

    /***
     * 定位完成回调
     * @param tencentLocation TencentLocation
     * @param i 状态码、
     * @param s 说明
     */
    @Override
    public void onLocationChanged(TencentLocation tencentLocation, int i, String s) {
        if(TencentLocation.ERROR_OK == i){
            LocationResult lr;
            // 定位成功
            if(tencentLocation != null){
                Log.v("com.zym.map.tencent", "定位成功！");
                //获取当前定位到的城市
                locationCity = tencentLocation.getCity();
                if(!initStatus){
                    currentLatLng = new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude());
                    moveLocation(currentLatLng.getLatitude(), currentLatLng.getLongitude());
                    initStatus = true;
                    selectLatLng = currentLatLng;
                    pageIndex = 1;
                    getAddress();
                }
                showThisLocation(tencentLocation);
                lr = new LocationResult();
                lr.setCode(0);
                lr.setMsg(s);
                lr.setLat(tencentLocation.getLatitude());
                lr.setLng(tencentLocation.getLongitude());
                lr.setAddress(tencentLocation.getAddress());
                listener.locationResult(lr);
            }else{
                Log.v("com.zym.map.tencent", String.format("定位失败，失败信息：%s", s));
                lr = new LocationResult();
                lr.setCode(-1);
                lr.setMsg(s);
                listener.locationResult(lr);
            }
        }
    }

    /***
     * 定位完成回调
     * @param s
     * @param i 状态码、
     * @param s1
     */
    @Override
    public void onStatusUpdate(String s, int i, String s1) {
        Log.v("com.zym.map.tencent", String.format("定位失败，失败信息：%s,%d,%s", s, i, s1));
    }


    @Override
    public void onStart() {
        super.onStart();
        if(mMapView != null){
            mMapView.onStart();
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
    public void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        if(mMapView != null){
            mMapView.onStop();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mMapView != null){
            mMapView.onDestroy();
        }
        if(locationManager != null){
            locationManager.removeUpdates(this);
        }
    }

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
            moveLocation(currentLatLng.getLatitude(), currentLatLng.getLongitude());
        }
    }
}
