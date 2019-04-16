package com.sxhalo.PullCoal.db.entity;

import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.AssignType;
import com.sxhalo.PullCoal.model.InformationDepartment;

import java.io.Serializable;
import java.util.List;

/**
 * Created by amoldZhang on 2017/1/12.
 */
@Table("mine_product_model")
public class MineProduct implements Serializable {

//    "adress": "府谷县 大昌汗乡", //地址
//            "name": "榆林信息部", // 信息部/矿口 名称
//            "longitude": "113.230000",// 经度
//            "latitude": "36.260000",//纬度
//            "type": "1"  //1信息部   2 矿口

//    "picpath": "http://172.16.99.199:8080/lamadm/upload/1483682652963.png",  //矿口图片
//            "mineMouthName": "测试矿口", //矿口名称
//            "address": "测试一下数据看看情况呗", //通讯地址
//            "regionName": "陕西省铜川市王益区", //地区
//            "longtitude": "113.563000", //经度
//            "latitude": "55.000000", //纬度

    @PrimaryKey(AssignType.BY_MYSELF)
    @Column(value = "mine_mouth_id")
    private String mineMouthId;
    @Column(value = "mine_mouth_adress")
    private String adress;
    @Column(value = "mine_mouth_name")
    private String name;
    @Column(value = "mine_mouth_longitude")
    private String longitude;   //经度
    @Column(value = "mine_mouth_latitude")
    private String latitude;    //纬度
    @Column(value = "mine_mouth_type")
    private String type;

    private String picpath;   //矿口图片
    private String mineMouthName;  //矿口名称
    private String address;      //通讯地址
    private String regionName;   //地区
    private String longtitude; //经度   (矿口详情用)


    private List<InformationDepartment> infoDepartList;

    public MineProduct(){}

    public List<InformationDepartment> getInformationDepartment() {
        return infoDepartList;
    }

    public void setInformationDepartment(List<InformationDepartment> informationDepartment) {
        this.infoDepartList = informationDepartment;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(String longtitude) {
        this.longtitude = longtitude;
    }

    public List<InformationDepartment> getInfoDepartList() {
        return infoDepartList;
    }

    public void setInfoDepartList(List<InformationDepartment> infoDepartList) {
        this.infoDepartList = infoDepartList;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
