package com.sxhalo.PullCoal.model;

import com.google.gson.Gson;
import com.sxhalo.PullCoal.utils.GHLog;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by amoldZhang on 2016/12/12.
 */

public class Coal implements Serializable {


    /**
     * goodsId : 200000041000005920001
     * contactPhone : 17791889370
     * mineMouthName : 神广煤矿
     * coalName : 啊啊
     * quote : 0.0
     * imageUrl :
     * longitude : 110.36673456430435
     * coalSalesId : 20000004
     * calorificValue : 6000
     * differMinute : 2017-07-31
     * latitude : 39.05308482197967
     * companyName : 聚亿煤炭信息部
     * oneQuote : 45.0
     * isSelfRelease
     * isBought
     * releaseNum
     * mineMouthRegionName": "",
     * mineMouthAddress": "",
     * coalCategoryName
     * "consultingFee": "0",//等于0免费
     *"licenseMinute": "2天后失效",
     * isPay": "1"//0未支付，1已支付
     */


    private String goodsId;     //煤id
    private String mineMouthName; //矿口名称
    private String coalName;   // 煤名称
    private String quote;     // 价格
    private String longitude;//煤矿经度
    private String coalSalesId;
    private String mineMouthId;
    private String calorificValue;  //发热量
    private String differMinute;  // 更新时间
    private String latitude;//煤矿维度
    private String companyName; //信息部名称
    private String oneQuote;   //一票价格
    private String imageUrl;   //
    private String categoryImage;   // 默认图片
    private String isSelfRelease;
    private String isBought;
    private String releaseNum;
    private String mineMouthRegionName;
    private String mineMouthAddress;
    private String coalCategoryName;
    private String coalReportTime;//化验时间
    private String coalReportPicUrl;//化验单url
    private String coalReportInstitution;//化验单位
    private String salesLongitude;//信息部经度
    private String salesLatitude;//信息部维度
    private String coalSalePicUrl;//信息部图片
    private String mineMouthPicUrl;//矿口图片
    private String consultingFee;//等于0免费
    private String licenseMinute;// 已支付剩余多少天
    private String isPay;//0未支付，1已支付
    private String publishUserId;  //当前煤碳发布人Id
    private String publishUser; //发布人
    private String publishUserPhone; //发布人电话
    private String publishState; //发布状态
    private String totalMoisture; //全水份
    private String totalSulfur; //全硫份
    private String volatileValue; //挥发份
    private String fixedCarbon; //固定碳
    private String isCollection; //是否收藏 0、未收藏  1、已收藏


    public String getCategoryImage() {
        return categoryImage;
    }

    public void setCategoryImage(String categoryImage) {
        this.categoryImage = categoryImage;
    }

    private boolean selectContarst;

    public String getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(String isCollection) {
        this.isCollection = isCollection;
    }

    public boolean isSelectContarst() {
        return selectContarst;
    }

    public void setSelectContarst(boolean selectContarst) {
        this.selectContarst = selectContarst;
    }

    public String getTotalMoisture() {
        return totalMoisture;
    }

    public void setTotalMoisture(String totalMoisture) {
        this.totalMoisture = totalMoisture;
    }

    public String getTotalSulfur() {
        return totalSulfur;
    }

    public void setTotalSulfur(String totalSulfur) {
        this.totalSulfur = totalSulfur;
    }

    public String getVolatileValue() {
        return volatileValue;
    }

    public void setVolatileValue(String volatileValue) {
        this.volatileValue = volatileValue;
    }

    public String getFixedCarbon() {
        return fixedCarbon;
    }

    public void setFixedCarbon(String fixedCarbon) {
        this.fixedCarbon = fixedCarbon;
    }

    public String getPublishUserPhone() {
        return publishUserPhone;
    }

    public void setPublishUserPhone(String publishUserPhone) {
        this.publishUserPhone = publishUserPhone;
    }

    public String getPublishUserId() {
        return publishUserId;
    }

    public void setPublishUserId(String publishUserId) {
        this.publishUserId = publishUserId;
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

    public String getPublishUser() {
        return publishUser;
    }

    public void setPublishUser(String publishUser) {
        this.publishUser = publishUser;
    }

    public String getCoalSalePicUrl() {
        return coalSalePicUrl;
    }

    public void setCoalSalePicUrl(String coalSalePicUrl) {
        this.coalSalePicUrl = coalSalePicUrl;
    }

    public String getMineMouthPicUrl() {
        return mineMouthPicUrl;
    }

    public void setMineMouthPicUrl(String mineMouthPicUrl) {
        this.mineMouthPicUrl = mineMouthPicUrl;
    }

    public String getMineMouthId() {
        return mineMouthId;
    }

    public void setMineMouthId(String mineMouthId) {
        this.mineMouthId = mineMouthId;
    }

    public String getSalesLongitude() {
        return salesLongitude;
    }

    public void setSalesLongitude(String salesLongitude) {
        this.salesLongitude = salesLongitude;
    }

    public String getSalesLatitude() {
        return salesLatitude;
    }

    public void setSalesLatitude(String salesLatitude) {
        this.salesLatitude = salesLatitude;
    }

    public String getCoalReportTime() {
        return coalReportTime;
    }

    public void setCoalReportTime(String coalReportTime) {
        this.coalReportTime = coalReportTime;
    }

    public String getCoalReportPicUrl() {
        return coalReportPicUrl;
    }

    public void setCoalReportPicUrl(String coalReportPicUrl) {
        this.coalReportPicUrl = coalReportPicUrl;
    }

    public String getCoalReportInstitution() {
        return coalReportInstitution;
    }

    public void setCoalReportInstitution(String coalReportInstitution) {
        this.coalReportInstitution = coalReportInstitution;
    }

    public List<Coal> getCoalList(List<Map<String, Object>> list) {
        List<Coal> coalList = new ArrayList<Coal>();
        if (list.size() != 0){
            for (int i=0;i<list.size();i++){
                Map<String, Object> objectMap = list.get(i);
                if (objectMap.size() != 0){
                    coalList.add(getCoal(objectMap));
                }
            }
        }
        return coalList;
    }

    public Coal getCoal(Map<String, Object> stringObjectMap){
        Coal coal = new Coal();
        if (stringObjectMap.size() != 0){
            try {
                Gson gson = new Gson();
                String jsonString = gson.toJson(stringObjectMap);
                coal = gson.fromJson(jsonString,Coal.class);
            } catch (Exception e) {
                GHLog.e("数据解析有null",e.toString());
            }
        }
        return coal;
    }

    public String getPublishState() {
        return publishState;
    }

    public void setPublishState(String publishState) {
        this.publishState = publishState;
    }

    public String getMineMouthRegionName() {
        return mineMouthRegionName;
    }

    public void setMineMouthRegionName(String mineMouthRegionName) {
        this.mineMouthRegionName = mineMouthRegionName;
    }

    public String getMineMouthAddress() {
        return mineMouthAddress;
    }

    public void setMineMouthAddress(String mineMouthAddress) {
        this.mineMouthAddress = mineMouthAddress;
    }

    public String getCoalCategoryName() {
        return coalCategoryName;
    }

    public void setCoalCategoryName(String coalCategoryName) {
        this.coalCategoryName = coalCategoryName;
    }

    public String getIsSelfRelease() {
        return isSelfRelease;
    }

    public void setIsSelfRelease(String isSelfRelease) {
        this.isSelfRelease = isSelfRelease;
    }

    public String getIsBought() {
        return isBought;
    }

    public void setIsBought(String isBought) {
        this.isBought = isBought;
    }

    public String getReleaseNum() {
        return releaseNum;
    }

    public void setReleaseNum(String releaseNum) {
        this.releaseNum = releaseNum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }

    public String getMineMouthName() {
        return mineMouthName;
    }

    public void setMineMouthName(String mineMouthName) {
        this.mineMouthName = mineMouthName;
    }

    public String getCoalName() {
        return coalName;
    }

    public void setCoalName(String coalName) {
        this.coalName = coalName;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getCoalSalesId() {
        return coalSalesId;
    }

    public void setCoalSalesId(String coalSalesId) {
        this.coalSalesId = coalSalesId;
    }

    public String getCalorificValue() {
        return calorificValue;
    }

    public void setCalorificValue(String calorificValue) {
        this.calorificValue = calorificValue;
    }

    public String getDifferMinute() {
        return differMinute;
    }

    public void setDifferMinute(String differMinute) {
        this.differMinute = differMinute;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getOneQuote() {
        return oneQuote;
    }

    public void setOneQuote(String oneQuote) {
        this.oneQuote = oneQuote;
    }

}
