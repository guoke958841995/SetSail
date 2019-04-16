package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 *  我的地址实体
 * Created by amoldZhang on 2018/5/9.
 */

public class UserAddress implements Serializable{

//    "contactPerson": "白玉兰",
//            "contactPhone": "18152352555",
//            "regionCode": "610103000",
//            "regionName": "陕西省西安市碑林区",
//            "address": "友谊东路与太乙路十字西北角(路北)",
//            "addressName": "太乙路",
//            "isDefault": "1",
//            "addressId": "1"

    private String contactPerson;
    private String contactPhone;
    private String regionCode;
    private String regionName;
    private String fullRegionName;
    private String address;
    private String longitude;
    private String latitude;
    private String addressName;
    private String isDefault;
    private String addressId;
    private String cityName;


    public String getFullRegionName() {
        return fullRegionName;
    }

    public void setFullRegionName(String fullRegionName) {
        this.fullRegionName = fullRegionName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressName() {
        return addressName;
    }

    public void setAddressName(String addressName) {
        this.addressName = addressName;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public String getAddressId() {
        return addressId;
    }

    public void setAddressId(String addressId) {
        this.addressId = addressId;
    }
}
