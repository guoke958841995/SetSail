package com.sxhalo.PullCoal.bean;

import com.sxhalo.PullCoal.utils.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2017/8/18.
 */
public class TransportMode extends InformationDepartment implements Serializable {


    /**
     * vehicleMode : 2
     * vehicleLength : 9.6
     * remark : 快来快来
     * toLongitude :
     * completeNum :
     * surplusNum : 55
     * toPlace : 陕西省榆林市府谷县
     * contactPhone : 17791889370
     * transportId : 220170818151157
     * fromPlace : 陕西省榆林市神木县麻家塔乡
     * fromLongitude :
     * companyName : 聚亿煤炭信息部
     * toLatitude :
     * cost : 555.0
     * publishTag : 免装卸,大量要车,秒装,跪求,现货
     * costUnit : 元/件
     * fromLatitude :
     * totalNum : 55
     * isParticipant :
     * vehicleLoad :
     * fromAddress : 赵仓峁村举人山
     * differMinute : 8小时前
     * contactPerson : 123
     * goodsName : 煤
     * toAddress : 咯鱼
     * freightType : 0
     */

    private String transportType;
    private String vehicleMode;
    private String vehicleLength;
    private String remark;
    private String toLongitude;
    private String completeNum;
    private String surplusNum;
    private String toPlace;
    private String transportId;
    private String fromPlace;
    private String fromLongitude;
    private String toLatitude;
    private String cost;
    private String publishTag;
    private String costUnit;
    private String fromLatitude;
    private String totalNum;
    private String isParticipant;
    private String isSelfRelease;  //是否可抢单
    private String vehicleLoad;
    private String fromAddress;
    private String differMinute;
    private String goodsName;
    private String toAddress;
    private String treatMemo; //拒绝原因
    private String publishUser; //发布人
    private String publishUserPhone; //发布人电话
    private String publishState; //发布状态

    private String informationCharge; //信息费
    private String loadingCharge; //装车费
    private String unloadingCharge; //卸车费
    private String distance; //距离

    private String consultingFee;//等于0免费
    private String licenseMinute;// 已支付剩余多少天
    private String isPay;//0未支付，1已支付
    private String lmbPublish;//是否是拉煤宝发布 0不是，1是

    /**
     *  货运单
     * transportOrderCode : 2201708191158095650
     * createTime : 2017-08-19 11:58:09
     * orderState : 0
     * carryTime :
     * orderType : 0
     * arriveTime :
     */

    private String transportOrderCode;
    private String createTime;
    private String orderState;
    private String carryTime;
    private String carryWeight;
    private String orderType;
    private String arriveTime;
    private String updateTime;
    private String carryWeightDocPicUrl;


    public List<TransportMode> getTransportList(List<Map<String, Object>> list) {
        List<TransportMode> myListData = new ArrayList<TransportMode>();
        for (int k=0;k<list.size();k++){
            Map<String, Object> objectMap = list.get(k);
            if (objectMap.size() != 0){
                TransportMode transport = new TransportMode().getTransportMode(objectMap);
                myListData.add(transport);
            }
        }
        return myListData;
    }

    public TransportMode getTransportMode(Map<String, Object> stringObjectMap) {
        TransportMode transportMode = new TransportMode();
        if (stringObjectMap.size() != 0){
            try {
                transportMode.setTransportId(stringObjectMap.get("transportId").toString());
                transportMode.setFromPlace(stringObjectMap.get("fromPlace").toString());
                transportMode.setPublishUserPhone(StringUtils.isEmpty(stringObjectMap.get("publishUserPhone"))? "" : stringObjectMap.get("publishUserPhone").toString());
                transportMode.setPublishUser(StringUtils.isEmpty(stringObjectMap.get("publishUser"))? "" : stringObjectMap.get("publishUser").toString());
                transportMode.setToPlace(stringObjectMap.get("toPlace").toString());
                transportMode.setDifferMinute(stringObjectMap.get("differMinute").toString());
                transportMode.setCompanyName(stringObjectMap.get("companyName").toString());
                transportMode.setCost(stringObjectMap.get("cost").toString());
                transportMode.setCostUnit(stringObjectMap.get("costUnit").toString());
                transportMode.setGoodsName(stringObjectMap.get("goodsName").toString());
                transportMode.setSurplusNum(stringObjectMap.get("surplusNum").toString());
                transportMode.setTotalNum(stringObjectMap.get("totalNum").toString());
                transportMode.setCompleteNum(stringObjectMap.get("completeNum").toString());
                transportMode.setTransportType(stringObjectMap.get("transportType").toString());
                transportMode.setToAddress(stringObjectMap.get("toAddress").toString());
                transportMode.setFromAddress(stringObjectMap.get("fromAddress").toString());
                transportMode.setPublishTag(stringObjectMap.get("publishTag").toString());
                transportMode.setUpdateTime(StringUtils.isEmpty(stringObjectMap.get("updateTime"))? "" : stringObjectMap.get("updateTime").toString());
                transportMode.setCarryWeightDocPicUrl(StringUtils.isEmpty(stringObjectMap.get("carryWeightDocPicUrl"))? "" : stringObjectMap.get("carryWeightDocPicUrl").toString());
                transportMode.setCarryWeight(StringUtils.isEmpty(stringObjectMap.get("carryWeight"))? "" : stringObjectMap.get("carryWeight").toString());
                transportMode.setInformationCharge(StringUtils.isEmpty(stringObjectMap.get("informationCharge"))? "" : stringObjectMap.get("informationCharge").toString());
                transportMode.setLoadingCharge(StringUtils.isEmpty(stringObjectMap.get("loadingCharge"))? "" : stringObjectMap.get("loadingCharge").toString());
                transportMode.setUnloadingCharge(StringUtils.isEmpty(stringObjectMap.get("unloadingCharge"))? "" : stringObjectMap.get("unloadingCharge").toString());
                transportMode.setPublishState(StringUtils.isEmpty(stringObjectMap.get("publishState"))? "" : stringObjectMap.get("publishState").toString());
                transportMode.setConsultingFee(stringObjectMap.get("consultingFee").toString());
                transportMode.setLicenseMinute(stringObjectMap.get("licenseMinute").toString());
                transportMode.setIsPay(stringObjectMap.get("isPay").toString());
                transportMode.setLmbPublish(stringObjectMap.get("lmbPublish").toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return transportMode;
    }

    public String getLmbPublish() {
        return lmbPublish;
    }

    public void setLmbPublish(String lmbPublish) {
        this.lmbPublish = lmbPublish;
    }

    public String getPublishState() {
        return publishState;
    }

    public void setPublishState(String publishState) {
        this.publishState = publishState;
    }

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public String getPublishUserPhone() {
        return publishUserPhone;
    }

    public void setPublishUserPhone(String publishUserPhone) {
        this.publishUserPhone = publishUserPhone;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getInformationCharge() {
        return informationCharge;
    }

    public void setInformationCharge(String informationCharge) {
        this.informationCharge = informationCharge;
    }

    public String getLoadingCharge() {
        return loadingCharge;
    }

    public void setLoadingCharge(String loadingCharge) {
        this.loadingCharge = loadingCharge;
    }

    public String getUnloadingCharge() {
        return unloadingCharge;
    }

    public void setUnloadingCharge(String unloadingCharge) {
        this.unloadingCharge = unloadingCharge;
    }

    public String getTreatMemo() {
        return treatMemo;
    }

    public void setTreatMemo(String treatMemo) {
        this.treatMemo = treatMemo;
    }

    public String getTransportType() {
        return transportType;
    }

    public void setTransportType(String transportType) {
        this.transportType = transportType;
    }

    public String getIsSelfRelease() {
        return isSelfRelease;
    }

    public void setIsSelfRelease(String isSelfRelease) {
        this.isSelfRelease = isSelfRelease;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getToLongitude() {
        return toLongitude;
    }

    public void setToLongitude(String toLongitude) {
        this.toLongitude = toLongitude;
    }

    public String getCompleteNum() {
        return completeNum;
    }

    public void setCompleteNum(String completeNum) {
        this.completeNum = completeNum;
    }

    public String getSurplusNum() {
        return surplusNum;
    }

    public void setSurplusNum(String surplusNum) {
        this.surplusNum = surplusNum;
    }

    public String getToPlace() {
        return toPlace;
    }

    public void setToPlace(String toPlace) {
        this.toPlace = toPlace;
    }

//    public String getContactPhone() {
//        return contactPhone;
//    }
//
//    public void setContactPhone(String contactPhone) {
//        this.contactPhone = contactPhone;
//    }

    public String getTransportId() {
        return transportId;
    }

    public void setTransportId(String transportId) {
        this.transportId = transportId;
    }

    public String getFromPlace() {
        return fromPlace;
    }

    public void setFromPlace(String fromPlace) {
        this.fromPlace = fromPlace;
    }

    public String getFromLongitude() {
        return fromLongitude;
    }

    public void setFromLongitude(String fromLongitude) {
        this.fromLongitude = fromLongitude;
    }

//    public String getCompanyName() {
//        return companyName;
//    }
//
//    public void setCompanyName(String companyName) {
//        this.companyName = companyName;
//    }

    public String getCarryWeight() {
        return carryWeight;
    }

    public void setCarryWeight(String carryWeight) {
        this.carryWeight = carryWeight;
    }

    public String getToLatitude() {
        return toLatitude;
    }

    public void setToLatitude(String toLatitude) {
        this.toLatitude = toLatitude;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getPublishTag() {
        return publishTag;
    }

    public void setPublishTag(String publishTag) {
        this.publishTag = publishTag;
    }

    public String getCostUnit() {
        return costUnit;
    }

    public void setCostUnit(String costUnit) {
        this.costUnit = costUnit;
    }

    public String getFromLatitude() {
        return fromLatitude;
    }

    public void setFromLatitude(String fromLatitude) {
        this.fromLatitude = fromLatitude;
    }

    public String getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(String totalNum) {
        this.totalNum = totalNum;
    }

    public String getIsParticipant() {
        return isParticipant;
    }

    public void setIsParticipant(String isParticipant) {
        this.isParticipant = isParticipant;
    }

    public String getVehicleLoad() {
        return vehicleLoad;
    }

    public void setVehicleLoad(String vehicleLoad) {
        this.vehicleLoad = vehicleLoad;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getDifferMinute() {
        return differMinute;
    }

    public void setDifferMinute(String differMinute) {
        this.differMinute = differMinute;
    }

//    public String getContactPerson() {
//        return contactPerson;
//    }
//
//    public void setContactPerson(String contactPerson) {
//        this.contactPerson = contactPerson;
//    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getTransportOrderCode() {
        return transportOrderCode;
    }

    public void setTransportOrderCode(String transportOrderCode) {
        this.transportOrderCode = transportOrderCode;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getOrderState() {
        return orderState;
    }

    public void setOrderState(String orderState) {
        this.orderState = orderState;
    }

    public String getCarryTime() {
        return carryTime;
    }

    public void setCarryTime(String carryTime) {
        this.carryTime = carryTime;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }


    public String getCarryWeightDocPicUrl() {
        return carryWeightDocPicUrl;
    }

    public void setCarryWeightDocPicUrl(String carryWeightDocPicUrl) {
        this.carryWeightDocPicUrl = carryWeightDocPicUrl;
    }

    public String getConsultingFee() {
        return consultingFee;
    }

    public void setConsultingFee(String consultingFee) {
        this.consultingFee = consultingFee;
    }

    public String getLicenseMinute() {
        return licenseMinute;
    }

    public void setLicenseMinute(String licenseMinute) {
        this.licenseMinute = licenseMinute;
    }

    public String getIsPay() {
        return isPay;
    }

    public void setIsPay(String isPay) {
        this.isPay = isPay;
    }
}
