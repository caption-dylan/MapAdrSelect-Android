package com.zym.map.base;

/**
 * 用于设定 Fragment 需要实现的方法
 */
public interface ISelectAdr {
    /***
     * 定位方法
     */
    void location();

    /**
     * 移动选点到指定位置
     * @param lat 纬度
     * @param lng 经度
     */
    void moveLocation(double lat, double lng);

    /**
     * 设置监听
     * @param listener ISelectAdrListener
     */
    void setListener(ISelectAdrListener listener);

    /**
     * 获取定位城市
     * @return 城市名
     */
    String getLocationCity();

    /**
     * 设置中间定位图标
     * @param res
     */
    void setLocationCenterIcon(int res);

    /**
     * 设置右下角的回到当前图标
     * @param res
     */
    void setLocationBackIcon(int res);

    /**
     * 设置列表选中时显示的图标
     * @param res
     */
    void setRvSelectIcon(int res);
}
