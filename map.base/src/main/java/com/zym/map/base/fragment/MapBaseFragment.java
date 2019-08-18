package com.zym.map.base.fragment;

import android.view.View;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zym.map.base.ISelectAdr;
import com.zym.map.base.ISelectAdrListener;
import com.zym.map.base.adapter.AdapterAddress;
import com.zym.map.base.entity.AddressDetail;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment 基类
 */
public abstract class MapBaseFragment extends Fragment implements ISelectAdr {
    protected View mView;
    protected RecyclerView mRv;
    protected ImageView ivBackCurrent,ivMoveSelect;

    //记录当前是否已初始化过
    protected boolean initStatus = false;

    //地址选择事件监听
    protected ISelectAdrListener listener;

    //用于记录是否是点击下面的地址
    protected boolean isClick = false;

    protected List<AddressDetail> listAddress = new ArrayList<>();

    //数据源
    protected AdapterAddress adAddress;

    //当前定位城市
    protected String locationCity = "";

    /**
     * 初始化 RecyclerView
     */
    protected void initRecyclerView(){
        // 定义一个线性布局管理器
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        // 设置布局管理器
        mRv.setLayoutManager(manager);
        adAddress = new AdapterAddress(getContext(), listAddress, this::itemClickMove);
        //设置 Adapter
        mRv.setAdapter(adAddress);
    }

    /**
     * item点击事件
     * @param position 点击位置
     * @return AddressDetail
     */
    protected AddressDetail itemClick(int position){
        AddressDetail adSelect = listAddress.get(position);
        listener.clickItem(adSelect);
        return adSelect;
    }
    /**
     * item点击事件带移动事件
     * @param position 点击位置
     * @return AddressDetail
     */
    protected AddressDetail itemClickMove(int position){
        isClick = true;
        AddressDetail adSelect = itemClick(position);
        moveLocation(adSelect.getLat(), adSelect.getLng());
        return adSelect;
    }
}
