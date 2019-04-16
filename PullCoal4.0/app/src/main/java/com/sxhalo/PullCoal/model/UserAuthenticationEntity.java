package com.sxhalo.PullCoal.model;

import java.io.Serializable;

/**
 * Created by amoldZhang on 2017/8/14.
 */

public class UserAuthenticationEntity implements Serializable{


    //共有字段
    private String createTime;
    private String authState;
    private String verifyTime;
    private String userId;
    private String realName;

    //修改用户头像
    private String headPic;


    //实名认证
    private String identitycardId;  //身份证号
    private String identitycardFrontUrl;
    private String identitycardBackPic;
    private String identitycardBackUrl;
    private String identitycardFrontPic;
    private String realnameAuthState;

   //司机认证
    private String vehicleMode;
    private String vehicleLength;
    private String vehicleLicense;  //行驶证号
    private String driverLicenseUrl; //驾驶证照片连接
    private String driverLicenseId;  //驾驶证号
    private String brandName;
    private String vehicleLicenseAttachedUrl;   ////行驶证附页图片连接
    private String vehicleLicenseAttachedPic;
    private String carPhotoPic;
    private String vehicleLoad;
    private String vehicleLicenseHomeUrl;  //行驶证图片连接
    private String driverLicenseType;
    private String carPhotoUrl;
    private String plateNumber;
    private String driverLicensePicCode;
    private String vehicleLicenseHomePicCode;
    private String percentage;  //认证完成度
    private String verifyMemo;
    private String vehicleWeight;
    private String vehicleModeName;
    private String driverLicenseIdUrl;
    private String vehicleModePicUrl;
    private String driverComplete;    //驾驶员资料完善状态 0未完善、1已完善
    private String realNameComplete;  //实名资料完善状态 0未完善、1已完善
    private String vehicleComplete;   //车辆资料完善状态 0未完善、1已完善
    private String driverLicenseInitialTime;   //驾驶证初次领证日期
    private String registDate;   //行驶证注册日期
    private String owner;   //车辆所有人

    private String startRegionName;
    private String endRegionName;
    private String startRegion;
    private String endRegion;

    //车辆信息
    private String vehicleLicenseUrl;
    private String vehicleState;   //车辆启用状态 0禁用、1启用
    private String driverAuthState;   //司机身份认证状态：0、未认证，1、登记，2、认证成功 '


    public String getAuthState() {
        return authState;
    }

    public void setAuthState(String authState) {
        this.authState = authState;
    }

    public String getDriverAuthState() {
        return driverAuthState;
    }

    public String getRealnameAuthState() {
        return realnameAuthState;
    }

    public void setRealnameAuthState(String realnameAuthState) {
        this.realnameAuthState = realnameAuthState;
    }

    public void setDriverAuthState(String driverAuthState) {
        this.driverAuthState = driverAuthState;
    }

    public String getRegistDate() {
        return registDate;
    }

    public void setRegistDate(String registDate) {
        this.registDate = registDate;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDriverLicenseInitialTime() {
        return driverLicenseInitialTime;
    }

    public void setDriverLicenseInitialTime(String driverLicenseInitialTime) {
        this.driverLicenseInitialTime = driverLicenseInitialTime;
    }

    public String getStartRegionName() {
        return startRegionName;
    }

    public void setStartRegionName(String startRegionName) {
        this.startRegionName = startRegionName;
    }

    public String getEndRegionName() {
        return endRegionName;
    }

    public void setEndRegionName(String endRegionName) {
        this.endRegionName = endRegionName;
    }

    public String getStartRegion() {
        return startRegion;
    }

    public void setStartRegion(String startRegion) {
        this.startRegion = startRegion;
    }

    public String getEndRegion() {
        return endRegion;
    }

    public void setEndRegion(String endRegion) {
        this.endRegion = endRegion;
    }

    public String getDriverComplete() {
        return driverComplete;
    }

    public void setDriverComplete(String driverComplete) {
        this.driverComplete = driverComplete;
    }

    public String getRealNameComplete() {
        return realNameComplete;
    }

    public void setRealNameComplete(String realNameComplete) {
        this.realNameComplete = realNameComplete;
    }

    public String getVehicleComplete() {
        return vehicleComplete;
    }

    public void setVehicleComplete(String vehicleComplete) {
        this.vehicleComplete = vehicleComplete;
    }

    public String getVehicleState() {
        return vehicleState;
    }

    public void setVehicleState(String vehicleState) {
        this.vehicleState = vehicleState;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getVerifyMemo() {
        return verifyMemo;
    }

    public void setVerifyMemo(String verifyMemo) {
        this.verifyMemo = verifyMemo;
    }

    public String getVehicleWeight() {
        return vehicleWeight;
    }

    public void setVehicleWeight(String vehicleWeight) {
        this.vehicleWeight = vehicleWeight;
    }

    public String getVehicleModeName() {
        return vehicleModeName;
    }

    public void setVehicleModeName(String vehicleModeName) {
        this.vehicleModeName = vehicleModeName;
    }

    public String getDriverLicenseIdUrl() {
        return driverLicenseIdUrl;
    }

    public void setDriverLicenseIdUrl(String driverLicenseIdUrl) {
        this.driverLicenseIdUrl = driverLicenseIdUrl;
    }

    public String getVehicleModePicUrl() {
        return vehicleModePicUrl;
    }

    public void setVehicleModePicUrl(String vehicleModePicUrl) {
        this.vehicleModePicUrl = vehicleModePicUrl;
    }

    public String getVehicleLicenseUrl() {
        return vehicleLicenseUrl;
    }

    public void setVehicleLicenseUrl(String vehicleLicenseUrl) {
        this.vehicleLicenseUrl = vehicleLicenseUrl;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getIdentitycardId() {
        return identitycardId;
    }

    public void setIdentitycardId(String identitycardId) {
        this.identitycardId = identitycardId;
    }

    public String getIdentitycardFrontUrl() {
        return identitycardFrontUrl;
    }

    public void setIdentitycardFrontUrl(String identitycardFrontUrl) {
        this.identitycardFrontUrl = identitycardFrontUrl;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getVerifyTime() {
        return verifyTime;
    }

    public void setVerifyTime(String verifyTime) {
        this.verifyTime = verifyTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getIdentitycardBackPic() {
        return identitycardBackPic;
    }

    public void setIdentitycardBackPic(String identitycardBackPic) {
        this.identitycardBackPic = identitycardBackPic;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getIdentitycardBackUrl() {
        return identitycardBackUrl;
    }

    public void setIdentitycardBackUrl(String identitycardBackUrl) {
        this.identitycardBackUrl = identitycardBackUrl;
    }

    public String getIdentitycardFrontPic() {
        return identitycardFrontPic;
    }

    public void setIdentitycardFrontPic(String identitycardFrontPic) {
        this.identitycardFrontPic = identitycardFrontPic;
    }

    public String getVehicleMode() {
        return vehicleMode;
    }

    public void setVehicleMode(String vehicleMode) {
        this.vehicleMode = vehicleMode;
    }

    public String getVehicleLength() {
        return vehicleLength;
    }

    public void setVehicleLength(String vehicleLength) {
        this.vehicleLength = vehicleLength;
    }

    public String getVehicleLicense() {
        return vehicleLicense;
    }

    public void setVehicleLicense(String vehicleLicense) {
        this.vehicleLicense = vehicleLicense;
    }

    public String getDriverLicenseUrl() {
        return driverLicenseUrl;
    }

    public void setDriverLicenseUrl(String driverLicenseUrl) {
        this.driverLicenseUrl = driverLicenseUrl;
    }

    public String getDriverLicenseId() {
        return driverLicenseId;
    }

    public void setDriverLicenseId(String driverLicenseId) {
        this.driverLicenseId = driverLicenseId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getVehicleLicenseAttachedUrl() {
        return vehicleLicenseAttachedUrl;
    }

    public void setVehicleLicenseAttachedUrl(String vehicleLicenseAttachedUrl) {
        this.vehicleLicenseAttachedUrl = vehicleLicenseAttachedUrl;
    }

    public String getVehicleLicenseAttachedPic() {
        return vehicleLicenseAttachedPic;
    }

    public void setVehicleLicenseAttachedPic(String vehicleLicenseAttachedPic) {
        this.vehicleLicenseAttachedPic = vehicleLicenseAttachedPic;
    }

    public String getCarPhotoPic() {
        return carPhotoPic;
    }

    public void setCarPhotoPic(String carPhotoPic) {
        this.carPhotoPic = carPhotoPic;
    }

    public String getVehicleLoad() {
        return vehicleLoad;
    }

    public void setVehicleLoad(String vehicleLoad) {
        this.vehicleLoad = vehicleLoad;
    }

    public String getVehicleLicenseHomeUrl() {
        return vehicleLicenseHomeUrl;
    }

    public void setVehicleLicenseHomeUrl(String vehicleLicenseHomeUrl) {
        this.vehicleLicenseHomeUrl = vehicleLicenseHomeUrl;
    }

    public String getDriverLicenseType() {
        return driverLicenseType;
    }

    public void setDriverLicenseType(String driverLicenseType) {
        this.driverLicenseType = driverLicenseType;
    }

    public String getCarPhotoUrl() {
        return carPhotoUrl;
    }

    public void setCarPhotoUrl(String carPhotoUrl) {
        this.carPhotoUrl = carPhotoUrl;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getVehicleLicenseHomePicCode() {
        return vehicleLicenseHomePicCode;
    }

    public void setVehicleLicenseHomePicCode(String vehicleLicenseHomePicCode) {
        this.vehicleLicenseHomePicCode = vehicleLicenseHomePicCode;
    }

    public String getDriverLicensePicCode() {
        return driverLicensePicCode;
    }

    public void setDriverLicensePicCode(String driverLicensePicCode) {
        this.driverLicensePicCode = driverLicensePicCode;
    }
}
