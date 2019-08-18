package com.zym.map.base.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 自定义配置
 */
public class Customize implements Parcelable {
    /**
     * 背景颜色
     */
    private int bgColor;

    /**
     * 返回说明文字
     */
    private String backText;
    /**
     * 返回说明文字的颜色
     */
    private int backColor;

    /**
     * 标题文字
     */
    private String title;
    /**
     * 标题文字颜色
     */
    private int titleColor;

    /**
     * 搜索图标
     */
    private int searchIcon;

    /**
     * 确认完成按钮文字
     */
    private String confirmText;
    /**
     * 确认完成按钮背景
     */
    private int confirmBg;
    /**
     * 确认完成按钮文字颜色
     */
    private int confirmTextColor;

    /**
     * 中间固定的的定位图标
     */
    private int locationCenterIcon;

    /**
     * 右下角图标
     */
    private int locationBackIcon;

    /**
     * 列表中的选中显示的图标
     */
    private int rvSelectIcon;

    /**
     * 搜索界面的返回图标
     */
    private int searchBackIcon;

    /**
     * 搜索界面的输入框提示文字
     */
    private String searchInputHint;

    public Customize(){}
    protected Customize(Parcel in) {
        bgColor = in.readInt();
        backText = in.readString();
        backColor = in.readInt();
        title = in.readString();
        titleColor = in.readInt();
        searchIcon = in.readInt();
        confirmText = in.readString();
        confirmBg = in.readInt();
        confirmTextColor = in.readInt();
        locationCenterIcon = in.readInt();
        locationBackIcon = in.readInt();
        rvSelectIcon = in.readInt();
        searchBackIcon = in.readInt();
        searchInputHint = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bgColor);
        dest.writeString(backText);
        dest.writeInt(backColor);
        dest.writeString(title);
        dest.writeInt(titleColor);
        dest.writeInt(searchIcon);
        dest.writeString(confirmText);
        dest.writeInt(confirmBg);
        dest.writeInt(confirmTextColor);
        dest.writeInt(locationCenterIcon);
        dest.writeInt(locationBackIcon);
        dest.writeInt(rvSelectIcon);
        dest.writeInt(searchBackIcon);
        dest.writeString(searchInputHint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customize> CREATOR = new Creator<Customize>() {
        @Override
        public Customize createFromParcel(Parcel in) {
            return new Customize(in);
        }

        @Override
        public Customize[] newArray(int size) {
            return new Customize[size];
        }
    };

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public String getBackText() {
        return backText;
    }

    public void setBackText(String backText) {
        this.backText = backText;
    }

    public int getBackColor() {
        return backColor;
    }

    public void setBackColor(int backColor) {
        this.backColor = backColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }

    public int getSearchIcon() {
        return searchIcon;
    }

    public void setSearchIcon(int searchIcon) {
        this.searchIcon = searchIcon;
    }

    public String getConfirmText() {
        return confirmText;
    }

    public void setConfirmText(String confirmText) {
        this.confirmText = confirmText;
    }

    public int getConfirmBg() {
        return confirmBg;
    }

    public void setConfirmBg(int confirmBg) {
        this.confirmBg = confirmBg;
    }

    public int getConfirmTextColor() {
        return confirmTextColor;
    }

    public void setConfirmTextColor(int confirmTextColor) {
        this.confirmTextColor = confirmTextColor;
    }

    public int getLocationCenterIcon() {
        return locationCenterIcon;
    }

    public void setLocationCenterIcon(int locationCenterIcon) {
        this.locationCenterIcon = locationCenterIcon;
    }

    public int getLocationBackIcon() {
        return locationBackIcon;
    }

    public void setLocationBackIcon(int locationBackIcon) {
        this.locationBackIcon = locationBackIcon;
    }

    public int getRvSelectIcon() {
        return rvSelectIcon;
    }

    public void setRvSelectIcon(int rvSelectIcon) {
        this.rvSelectIcon = rvSelectIcon;
    }

    public int getSearchBackIcon() {
        return searchBackIcon;
    }

    public void setSearchBackIcon(int searchBackIcon) {
        this.searchBackIcon = searchBackIcon;
    }

    public String getSearchInputHint() {
        return searchInputHint;
    }

    public void setSearchInputHint(String searchInputHint) {
        this.searchInputHint = searchInputHint;
    }
}
