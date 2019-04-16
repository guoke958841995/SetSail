package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/8/8.
 */
public class UserEntity  implements Serializable {


    /**
     * regionCode :
     * driverTime : 8
     * vehicleMode : 1
     * vehicleLength : 15.0
     * orderTotal : 0
     * nickname : 郭先生
     * realnameAuthState : 1
     * transTotal : 4
     * regionName :
     * headPic : http://login.sxhalo.com:80/lamadm/upload/1484297937146.jpg
     * creditRating : 3
     * isFriend : 0
     * vehicleLoad : 50.0
     * userId : 80000256
     * driverState : 1
     * userMobile : 13753060133
     * userName : 13753060133
     * plateNumber : 陕AV5B88
     * realName : 郭先僧
     * salesTotal : 0
     * driverRegisterName : 地方方法
     * driverAuthState : 2  //
     */

    private String regionCode;
    private String driverTime;
    private String vehicleMode;
    private String vehicleLength;
    private String orderTotal;
    private String nickname;
    private String realnameAuthState;
    private String transTotal;
    private String regionName;
    private String headPic;
    private String creditRating;
    private String isFriend;  //1好友
    private String vehicleLoad;
    private String userId;
    private String coalSalesId; //用户所在的信息部id
    private String driverState;  //司机接单状态：1：空闲，2：忙碌
    private String userMobile;
    private String userName;
    private String plateNumber; //车牌号
    private String realName;
    private String salesTotal;
    private String driverRegisterName;
    private String driverAuthState;//0、未认证，1、登记，2、认证成功
    private String cumulative;//累计接单数
    private String carPhotoUrl;//认证图片
    private String createTime; //注册日期
    private String userPwd; //是否设置密码

    private String specialLine;
    private String startRegion;
    private String endRegion;

    private String startRegionName;
    private String endRegionName;

    private String roleId;
    private String brandName;//车辆品牌
    private String percentage;//资料完成度
    private String demandTotal;//买煤求购数

    private String realNameReviewStatus;//实名审核状态0审核完成或未进行、1审核中
    private String driverReviewStatus;//司机审核状态0审核完成或未进行、1审核中


    private String pocketAmount;//余额
    private String availableNumber;//可用优惠券张数
    private String escrowAmount;//煤款余额总数   //总金额
    private String uncashAmount;//不可提现金额    //可退款金额
    private String freezeUncashAmount;//不可提现冻结金额    //冻结金额
    private String wxOpenidLmb;
    private String weChatCredential;//登录/提现绑定的微信openId
    private String withdrawQuota; //当剩余提现金额


    private boolean selectUser;

    public String getUncashAmount() {
        return uncashAmount;
    }

    public void setUncashAmount(String uncashAmount) {
        this.uncashAmount = uncashAmount;
    }

    public String getFreezeUncashAmount() {
        return freezeUncashAmount;
    }

    public void setFreezeUncashAmount(String freezeUncashAmount) {
        this.freezeUncashAmount = freezeUncashAmount;
    }

    public String getWithdrawQuota() {
        return withdrawQuota;
    }

    public void setWithdrawQuota(String withdrawQuota) {
        this.withdrawQuota = withdrawQuota;
    }

    public boolean isSelectUser() {
        return selectUser;
    }

    public void setSelectUser(boolean selectUser) {
        this.selectUser = selectUser;
    }

    public String getWeChatCredential() {
        return weChatCredential;
    }

    public void setWeChatCredential(String weChatCredential) {
        this.weChatCredential = weChatCredential;
    }

    public String getPocketAmount() {
        return pocketAmount;
    }

    public void setPocketAmount(String pocketAmount) {
        this.pocketAmount = pocketAmount;
    }

    public String getAvailableNumber() {
        return availableNumber;
    }

    public void setAvailableNumber(String availableNumber) {
        this.availableNumber = availableNumber;
    }

    public String getEscrowAmount() {
        return escrowAmount;
    }

    public void setEscrowAmount(String escrowAmount) {
        this.escrowAmount = escrowAmount;
    }

    public String getWxOpenidLmb() {
        return wxOpenidLmb;
    }

    public void setWxOpenidLmb(String wxOpenidLmb) {
        this.wxOpenidLmb = wxOpenidLmb;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getUserPwd() {
        return userPwd;
    }

    public void setUserPwd(String userPwd) {
        this.userPwd = userPwd;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getRealNameReviewStatus() {
        return realNameReviewStatus;
    }

    public void setRealNameReviewStatus(String realNameReviewStatus) {
        this.realNameReviewStatus = realNameReviewStatus;
    }

    public String getDriverReviewStatus() {
        return driverReviewStatus;
    }

    public void setDriverReviewStatus(String driverReviewStatus) {
        this.driverReviewStatus = driverReviewStatus;
    }

    public String getDemandTotal() {
        return demandTotal;
    }

    public void setDemandTotal(String demandTotal) {
        this.demandTotal = demandTotal;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getCarPhotoUrl() {
        return carPhotoUrl;
    }

    public void setCarPhotoUrl(String carPhotoUrl) {
        this.carPhotoUrl = carPhotoUrl;
    }

    public String getCumulative() {
        return cumulative;
    }

    public void setCumulative(String cumulative) {
        this.cumulative = cumulative;
    }

    public String getSpecialLine() {
        return specialLine;
    }

    public void setSpecialLine(String specialLine) {
        this.specialLine = specialLine;
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

    public UserEntity getUserEntity(Map<String, Object> objectMap) {
        UserEntity userEntity = new UserEntity();
        if (objectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(objectMap);
                userEntity = gson.fromJson(jsonString,UserEntity.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userEntity;
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

    public String getRegionCode() {
        return regionCode;
    }

    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    public String getDriverTime() {
        return driverTime;
    }

    public void setDriverTime(String driverTime) {
        this.driverTime = driverTime;
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

    public String getOrderTotal() {
        return orderTotal;
    }

    public void setOrderTotal(String orderTotal) {
        this.orderTotal = orderTotal;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRealnameAuthState() {
        return realnameAuthState;
    }

    public void setRealnameAuthState(String realnameAuthState) {
        this.realnameAuthState = realnameAuthState;
    }

    public String getTransTotal() {
        return transTotal;
    }

    public void setTransTotal(String transTotal) {
        this.transTotal = transTotal;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getHeadPic() {
        return headPic;
    }

    public void setHeadPic(String headPic) {
        this.headPic = headPic;
    }

    public String getCreditRating() {
        return creditRating;
    }

    public void setCreditRating(String creditRating) {
        this.creditRating = creditRating;
    }

    public String getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(String isFriend) {
        this.isFriend = isFriend;
    }

    public String getVehicleLoad() {
        return vehicleLoad;
    }

    public void setVehicleLoad(String vehicleLoad) {
        this.vehicleLoad = vehicleLoad;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getDriverState() {
        return driverState;
    }

    public void setDriverState(String driverState) {
        this.driverState = driverState;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPlateNumber() {
        return plateNumber;
    }

    public void setPlateNumber(String plateNumber) {
        this.plateNumber = plateNumber;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getSalesTotal() {
        return salesTotal;
    }

    public void setSalesTotal(String salesTotal) {
        this.salesTotal = salesTotal;
    }

    public String getDriverRegisterName() {
        return driverRegisterName;
    }

    public void setDriverRegisterName(String driverRegisterName) {
        this.driverRegisterName = driverRegisterName;
    }

    public String getDriverAuthState() {
        return driverAuthState;
    }

    public void setDriverAuthState(String driverAuthState) {
        this.driverAuthState = driverAuthState;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }
}
