package com.zym.map.base.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressDetail implements Parcelable {
    private String title;
    private String detail;
    private double lat;
    private double lng;

    public AddressDetail(){}

    protected AddressDetail(Parcel in) {
        title = in.readString();
        detail = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(detail);
        dest.writeDouble(lat);
        dest.writeDouble(lng);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AddressDetail> CREATOR = new Creator<AddressDetail>() {
        @Override
        public AddressDetail createFromParcel(Parcel in) {
            return new AddressDetail(in);
        }

        @Override
        public AddressDetail[] newArray(int size) {
            return new AddressDetail[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
