package com.zym.map.base.entity;

import java.util.List;

/**
 * 逆地址解析
 */
public class ReverseAddress {
    private int status;
    private String message;
    private String request_id;
    private Result result;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRequest_id() {
        return request_id;
    }

    public void setRequest_id(String request_id) {
        this.request_id = request_id;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public class Result{
        private Location location;
        private String address;
        private FormattedAddresses formatted_addresses;
        private AddressComponent address_component;
        private List<Pois> pois;

        public List<Pois> getPois() {
            return pois;
        }

        public void setPois(List<Pois> pois) {
            this.pois = pois;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public FormattedAddresses getFormatted_addresses() {
            return formatted_addresses;
        }

        public void setFormatted_addresses(FormattedAddresses formatted_addresses) {
            this.formatted_addresses = formatted_addresses;
        }

        public AddressComponent getAddress_component() {
            return address_component;
        }

        public void setAddress_component(AddressComponent address_component) {
            this.address_component = address_component;
        }
    }
    public class Location{
        private double lat;
        private double lng;

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
    public class FormattedAddresses{
        private String recommend;
        private String rough;

        public String getRecommend() {
            return recommend;
        }

        public void setRecommend(String recommend) {
            this.recommend = recommend;
        }

        public String getRough() {
            return rough;
        }

        public void setRough(String rough) {
            this.rough = rough;
        }
    }
    public class AddressComponent{
        private String nation;
        private String province;
        private String city;
        private String district;
        private String street;
        private String street_number;

        public String getNation() {
            return nation;
        }

        public void setNation(String nation) {
            this.nation = nation;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getStreet() {
            return street;
        }

        public void setStreet(String street) {
            this.street = street;
        }

        public String getStreet_number() {
            return street_number;
        }

        public void setStreet_number(String street_number) {
            this.street_number = street_number;
        }
    }
    public class Pois{
        private String id;
        private String title;
        private String address;
        private String category;
        private Location location;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }
}
