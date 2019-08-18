package com.zym.map.base.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.zym.map.base.R;
import com.zym.map.base.entity.AddressDetail;

import java.util.List;

public class AdapterAddress extends RecyclerView.Adapter {

    private Context mContext;
    private List<AddressDetail> listAddress;
    private int selectIndex = -1;
    private int selectIcon;
    private IOnClickListener listener;


    /**
     * 设置高亮关键字
     * @param highlight
     */
    public void setHighlight(boolean highlight) {
        isHighlight = highlight;
    }

    /**
     * 设置关键字
     * @param keyword
     */
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    /**
     * 设置当前选中下标
     * @param index
     */
    public void setSelectIndex(int index){
        selectIndex = index;
    }

    /**
     * 设置选中时显示的图标
     * @param res
     */
    public void setSelectIcon(int res){
        selectIcon = res;
    }

    private boolean isHighlight = false;
    private String keyword = "";

    public AdapterAddress(Context context, List<AddressDetail> listAddress, IOnClickListener listener){
        mContext = context;
        this.listAddress = listAddress;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.map_base_item, parent, false);
        return new AddressViewHolder(view);
    }

    private int rgb = Color.rgb(75, 192, 101);
    private ForegroundColorSpan fcs = new ForegroundColorSpan(rgb);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        AddressDetail detail = listAddress.get(position);
        AddressViewHolder avh = ((AddressViewHolder)holder);
        String title = detail.getTitle(),
                address = detail.getDetail();
        if(isHighlight){
            SpannableStringBuilder spannableTitle = new SpannableStringBuilder(title);
            int index = title.indexOf(keyword);
            if(index != -1){
                spannableTitle.setSpan(fcs, index, (index + keyword.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                avh.tvTitle.setText(spannableTitle);
            }else{
                avh.tvTitle.setText(title);
            }

            SpannableStringBuilder spannableDetail = new SpannableStringBuilder(address);
            index = address.indexOf(keyword);
            if(index != -1){
                spannableDetail.setSpan(fcs, index, (index + keyword.length()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                avh.tvDetail.setText(spannableDetail);
            }else {
                avh.tvDetail.setText(address);
            }
        }else {
            avh.tvTitle.setText(title);
            avh.tvDetail.setText(address);
        }

        if(!isHighlight){
            if(selectIndex == position){
                if(selectIcon != 0){
                    avh.ivSelect.setImageResource(selectIcon);
                }
                avh.ivSelect.setVisibility(View.VISIBLE);
            }else {
                avh.ivSelect.setVisibility(View.GONE);
            }
        }

        avh.itemView.setOnClickListener(v -> {
            if(!isHighlight){
                int tmpPosition = selectIndex;
                selectIndex = position;
                notifyItemChanged(tmpPosition);
                notifyItemChanged(position);
            }
            listener.onClickItem(position);
        });
    }

    @Override
    public int getItemCount() {
        return listAddress.size();
    }

    private class AddressViewHolder extends RecyclerView.ViewHolder{

        private TextView tvTitle;
        private TextView tvDetail;
        private ImageView ivSelect;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDetail = itemView.findViewById(R.id.tv_detail);
            ivSelect = itemView.findViewById(R.id.iv_select);
        }
    }

    public interface IOnClickListener{
        /**
         * item 的点击事件
         * @param position
         */
        void onClickItem(int position);
    }
}
