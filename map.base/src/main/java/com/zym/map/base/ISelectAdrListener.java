package com.zym.map.base;

import com.zym.map.base.entity.AddressDetail;
import com.zym.map.base.entity.LocationResult;

public interface ISelectAdrListener {
    /***
     * 定位完成
     * @param lr LocationResult
     */
    void locationResult(LocationResult lr);

    /**
     * rv item 点击事件
     * @param ad
     */
    void clickItem(AddressDetail ad);

    /**
     * 初始化完成 onCreateView 方法
     */
    void createReady();
}
