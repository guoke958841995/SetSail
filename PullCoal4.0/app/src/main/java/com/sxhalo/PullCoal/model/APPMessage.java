package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2018/1/22.
 */

public class APPMessage implements Serializable{

    /**
     * title : 用户图片上传
     * dataType : user030004
     * driverLicenseUrl : http://172.16.99.199:8080/picservice/picture/8b94b694-8ce2-4588-895e-0279c2d98c3f
     * driverLicensePicCode : 8b94b694-8ce2-4588-895e-0279c2d98c3f
     */

    private String title;
    private String dataType;
    private String driverLicenseUrl;
    private String driverLicensePicCode;

    private String vehicleLicenseHomeUrl;
    private String vehicleLicenseHomePicCode;

    private String vehicleLicenseAttachedUrl;
    private String carPhotoUrl;

    private String headPic;

    private String identitycardFrontUrl;
    private String identitycardBackUrl;


    public String getVehicleLicenseHomeUrl() {
        return vehicleLicenseHomeUrl;
    }

    public void setVehicleLicenseHomeUrl(String vehicleLicenseHomeUrl) {
        this.vehicleLicenseHomeUrl = vehicleLicenseHomeUrl;
    }

    public String getVehicleLicenseHomePicCode() {
        return vehicleLicenseHomePicCode;
    }

    public void setVehicleLicenseHomePicCode(String vehicleLicenseHomePicCode) {
        this.vehicleLicenseHomePicCode = vehicleLicenseHomePicCode;
    }

    public String getVehicleLicenseAttachedUrl() {
        return vehicleLicenseAttachedUrl;
    }

    public void setVehicleLicenseAttachedUrl(String vehicleLicenseAttachedUrl) {
        this.vehicleLicenseAttachedUrl = vehicleLicenseAttachedUrl;
    }

    public String getCarPhotoUrl() {
        return carPhotoUrl;
    }

    public void setCarPhotoUrl(String carPhotoUrl) {
        this.carPhotoUrl = carPhotoUrl;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getIdentitycardFrontUrl() {
        return identitycardFrontUrl;
    }

    public void setIdentitycardFrontUrl(String identitycardFrontUrl) {
        this.identitycardFrontUrl = identitycardFrontUrl;
    }

    public String getIdentitycardBackUrl() {
        return identitycardBackUrl;
    }

    public void setIdentitycardBackUrl(String identitycardBackUrl) {
        this.identitycardBackUrl = identitycardBackUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getDriverLicenseUrl() {
        return driverLicenseUrl;
    }

    public void setDriverLicenseUrl(String driverLicenseUrl) {
        this.driverLicenseUrl = driverLicenseUrl;
    }

    public String getDriverLicensePicCode() {
        return driverLicensePicCode;
    }

    public void setDriverLicensePicCode(String driverLicensePicCode) {
        this.driverLicensePicCode = driverLicensePicCode;
    }
}
